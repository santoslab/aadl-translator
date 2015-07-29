package edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class ModelUtil {
	

	
	public final static Predicate<ExchangeModel> getExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof GetExchangeModel);
		}
	};
	
	public final static Function<ExchangeModel, GetExchangeModel> transformToGetExchangeModel = new Function<ExchangeModel, GetExchangeModel>(){
		public GetExchangeModel apply(ExchangeModel em){
			return ((GetExchangeModel) em);
		}
	};
	
	public final static Predicate<ExchangeModel> setExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof SetExchangeModel);
		}
	};
	
	public final static Function<ExchangeModel, SetExchangeModel> transformToSetExchangeModel = new Function<ExchangeModel, SetExchangeModel>(){
		public SetExchangeModel apply(ExchangeModel em){
			return ((SetExchangeModel) em);
		}
	};
	
	public final static Predicate<ExchangeModel> actionExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof ActionExchangeModel);
		}
	};
	
	public final static Function<ExchangeModel, ActionExchangeModel> transformToActionExchangeModel = new Function<ExchangeModel, ActionExchangeModel>(){
		public ActionExchangeModel apply(ExchangeModel em){
			return ((ActionExchangeModel) em);
		}
	};
	
	public final static Predicate<ExchangeModel> periodicExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof PeriodicExchangeModel);
		}
	};
	
	public final static Function<ExchangeModel, PeriodicExchangeModel> transformToPeriodicExchangeModel = new Function<ExchangeModel, PeriodicExchangeModel>(){
		public PeriodicExchangeModel apply(ExchangeModel em){
			return ((PeriodicExchangeModel) em);
		}
	};
	
	public final static Predicate<ExchangeModel> sporadicExchangeFilter = new Predicate<ExchangeModel>() {
		public boolean apply(ExchangeModel em) {
			return (em instanceof SporadicExchangeModel);
		}
	};
	
	public final static Function<ExchangeModel, SporadicExchangeModel> transformToSporadicExchangeModel = new Function<ExchangeModel, SporadicExchangeModel>(){
		public SporadicExchangeModel apply(ExchangeModel em){
			return ((SporadicExchangeModel) em);
		}
	};
}
