package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Predicate;

public class ModelUtil {
	
	private final static Map<String, String> Default_Value_Dictionary = 
			Collections.unmodifiableMap(new HashMap<String, String>(){/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			{
				put("int", "0");
				put("float", "0.0");
				put("SpO2", "0");
				put("PulseRate", "0");
			}});
	
	public static String getDefaultValueString(String type){
		return Default_Value_Dictionary.get(type);
	}
	
	public final static Predicate<ExchangeModel> getExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof GetExchangeModel);
		}
	};
	
	public final static Predicate<ExchangeModel> setExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof SetExchangeModel);
		}
	};
	
	public final static Predicate<ExchangeModel> actionExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof ActionExchangeModel);
		}
	};
	
	public final static Predicate<ExchangeModel> periodicExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof PeriodicExchangeModel);
		}
	};
	
	public final static Predicate<ExchangeModel> sporadicExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof SporadicExchangeModel);
		}
	};
}
