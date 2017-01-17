var WIDGETVAR_FATHER_PREFIX = "wvFather-";
var WIDGETVAR_SON_PREFIX = "wvSon-";
var DATA_CATNAME = "catName";
var CATEGO_ID_FATHER_PREFIX = "lineForm:fatherCategory";
var CATEGO_ID_FATHER_PREFIX_ESCAPED = "lineForm\\:fatherCategory";
var CATEGO_ID_SON_PREFIX = "lineForm:lineSonCategory";
var SELECTOR_FOR_INPUT_WITH_ACTUAL_VALUE = "input.ui-selectonemenu-label";

function warningForCatForm(){
	// warning for replaceCat
	
	// warning for setting fatherCat if there already are sonCats
}

function hideWarnings(){
	$("#warningBeforeCatReplace").hide();
	$("#warningBeforeChoosingFatherWhileWithChild").hide();
	$(".amountInput").removeClass("autoCompleteTarget warnAutoCompleteTarget infoAutoCompleteTarget");
}
