package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;

import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.properties.PropertyNotPresentException;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.Translator.TranslatorSwitch;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.PropertyOutOfRangeException;

public class TranslatorUtil {

	public enum PropertyType {
		ENUM, INT, RANGE_MIN, RANGE_MAX, STRING
	};

	/**
	 * Returns the value of a custom (ie, non-library) property, or null if no
	 * property is found
	 * 
	 * @param obj
	 *            The element that may contain the property
	 * @param propertyName
	 *            The name of the property
	 * @param propType
	 *            The type of the property
	 * @return The property value or null if the property isn't found
	 */
	public static String checkCustomProperty(NamedElement obj, String propertyName, ArrayList<String> propertySetNames,
			PropertyType propType, TranslatorSwitch trans) {
		String ret = null;
		Property prop;
		for (String propertySetName : propertySetNames) {
			try {
				prop = GetProperties.lookupPropertyDefinition(obj, propertySetName, propertyName);
				if (prop == null)
					continue;
				else
					ret = handlePropertyValue(obj, prop, propType);
			} catch (PropertyOutOfRangeException e) {
				trans.handleException(obj, e);
				return null;
			} catch (PropertyNotPresentException e) {
				return null;
			}
		}
		return ret;
	}

	public static String handlePropertyValue(NamedElement obj, Property prop, PropertyType propType)
			throws PropertyOutOfRangeException {
		if (propType == PropertyType.ENUM)
			return PropertyUtils.getEnumLiteral(obj, prop).getName();
		else if (propType == PropertyType.INT) {
			// Should you ever need to get the unit of a property, this is
			// how you can do it. This example needs a better home, but it
			// took me so long to figure out that I can't just delete it.
			//
			// NumberValue nv =
			// (NumberValue)PropertyUtils.getSimplePropertyValue(obj, prop);
			// nv.getUnit()

			return getStringFromScaledNumber(
					PropertyUtils.getScaledNumberValue(obj, prop, GetProperties.findUnitLiteral(prop, "ms")), obj,
					prop);
		} else if (propType == PropertyType.RANGE_MIN) {
			return getStringFromScaledNumber(
					PropertyUtils.getScaledRangeMinimum(obj, prop, GetProperties.findUnitLiteral(prop, "ms")), obj,
					prop);
		} else if (propType == PropertyType.RANGE_MAX) {
			return getStringFromScaledNumber(
					PropertyUtils.getScaledRangeMaximum(obj, prop, GetProperties.findUnitLiteral(prop, "ms")), obj,
					prop);
		} else if (propType == PropertyType.STRING) {
			return PropertyUtils.getStringValue(obj, prop);
		} else {
			System.err.println("HandlePropertyValue called with garbage propType: " + propType);
		}
		return null;
	}

	private static String getStringFromScaledNumber(double num, NamedElement obj, Property prop)
			throws PropertyOutOfRangeException {
		if (num == (int) Math.rint(num))
			return String.valueOf((int) Math.rint(num));
		else
			throw new PropertyOutOfRangeException("Property " + prop.getName() + " on element " + obj.getName()
					+ " converts to " + num + " ms, which cannot be converted to an integer");
	}

}
