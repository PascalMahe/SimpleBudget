var WIDGETVAR_FATHER_PREFIX = "wvFather-";
var WIDGETVAR_SON_PREFIX = "wvSon-";
var DATA_CATNAME = "catName";
var CATEGO_ID_FATHER_PREFIX = "lineForm:fatherCategory";
var CATEGO_ID_FATHER_PREFIX_ESCAPED = "lineForm\\:fatherCategory";
var CATEGO_ID_SON_PREFIX = "lineForm:lineSonCategory";
var SELECTOR_FOR_INPUT_WITH_ACTUAL_VALUE = "input.ui-selectonemenu-label";

function warningForLineForm(){
	
	// if there's one (and only one) amount that's empty or 0 or 0.0
	// display a warning saying it's gonna be autoCompleted
	// if there's more than one, say they're gonna be left alone
	
	var listElmtWithZeroAmount = [];
	var listAmountInput = $(".amountInput"); 
	listAmountInput.each(function(){
		var currentElmt = $(this);
		console.log("currentElmt.val: " + currentElmt.val());
		var currentElmtValue = currentElmt.val();
		if(currentElmtValue === '' || 
				currentElmtValue === "0.0" || 
				currentElmtValue === "0" || 
				currentElmtValue === 0.0 || 
				currentElmtValue === 0){
			listElmtWithZeroAmount.push(currentElmt);
		}
	});
	
	if(listElmtWithZeroAmount.length >= 1){
		var multipleCats = listAmountInput.length > 1;
		
		var classToAdd = "autoCompleteTarget ";
		var messageID;
		if(listElmtWithZeroAmount.length == 1){
			// shine it! (blue)
			classToAdd += "infoAutoCompleteTarget";
			if(multipleCats){
				messageID = "autoCompleteMsgMultiCat";
			} else {
				messageID = "autoCompleteMsgOneCat";
			}
		} else if(listElmtWithZeroAmount.length > 1){
			// shine'm! (yellow)
			classToAdd += "warnAutoCompleteTarget";
			messageID = "infoNoAutoCompleteMsg";
		}
		
		// actual change of CSS class
		var firstElmt;
		for(var i = 0; i < listElmtWithZeroAmount.length; i++){
			var elmtWithZeroAmount = listElmtWithZeroAmount[i];
			elmtWithZeroAmount.addClass(classToAdd);
			if(i === 0){
				firstElmt = elmtWithZeroAmount;
			}
		}
		
		// moving the message
		var autoCompleteMsg = $("#" + messageID);
		var msgTop = firstElmt.offset().top; // top is same as first catChoice's top
		var categoryZone = $(".categoryZone"); // left is right of categoryZone
		
		var pos = categoryZone.position();
		var msgLeft = categoryZone.offset().left + categoryZone.outerWidth();
		autoCompleteMsg.css({'top': msgTop + "px", 'left': msgLeft + "px"});
		autoCompleteMsg.show();
	}
}

function hideWarnings(){
	$("#autoCompleteMsgOneCat").hide();
	$("#autoCompleteMsgMultiCat").hide();
	$("#infoNoAutoCompleteMsg").hide();
	$(".amountInput").removeClass("autoCompleteTarget warnAutoCompleteTarget infoAutoCompleteTarget");
}

function getActualValueSon(sonElement){
	var categoId = sonElement.attr("id");
	categoId = categoId.replace(CATEGO_ID_SON_PREFIX, "");

	var inputWithActualValueElmt = sonElement.find(SELECTOR_FOR_INPUT_WITH_ACTUAL_VALUE);

	var actualValue = inputWithActualValueElmt.val();
	
	var fatherElmt = $("#" + CATEGO_ID_FATHER_PREFIX_ESCAPED + categoId);

	var currentFatherVal = getActualValueFather(fatherElmt);
	actualValue = currentFatherVal + ":" + actualValue;
	return actualValue;
}


function getActualValueFather(fatherElement){
	var categoId = fatherElement.attr("id");
	categoId = categoId.replace(CATEGO_ID_FATHER_PREFIX, "");
	var inputWithActualValueElmt = fatherElement.find(SELECTOR_FOR_INPUT_WITH_ACTUAL_VALUE);

	var actualValue = inputWithActualValueElmt.val();
	return actualValue;
}

function setValid(catChoiceElement){
	catChoiceElement.removeClass("invalidField");
	catChoiceElement.addClass("validField");
}

function setInvalid(catChoiceElement){
	catChoiceElement.addClass("invalidField");
	catChoiceElement.removeClass("validField");
}


function validateCatChoices(){
	
	console.log("validateCatChoices - Start...");
		
	// loop on sons:
	//			set $this valid
	//			get their father:son value (see armCategoryNames function for technique)
	//			check if in map
	//				-> if it is, make $this and $oneInMap invalid
	//					$nbInvalidPairs++ 
	//				-> if not, put in map
	//		loop on father
	//			set $this valid
	//			get their actual value
	//			check if key in map that is son of theirs
	//			if not, check if in map
	//				-> if it is, make $this and $oneInMap invalid
	//					$nbInvalidPairs++
	//				-> if not, put in map
	
	var nbInvalidPairs = 0;
	var mapCat = {};
	var firstElmt = null; // for th position of the error message
	
	var listSons = $(".catChoiceSon");
	listSons.each(function() {

		var currentCatChoice = $(this);
		setValid(currentCatChoice);
		var currentValue = getActualValueSon(currentCatChoice);
		console.log("validateCatChoices - son with value: '" + currentValue + "'...");
		if(mapCat[currentValue]){
			var sameCategoryInMap = mapCat[currentValue];
			setInvalid(currentCatChoice);
			setInvalid(sameCategoryInMap);
			nbInvalidPairs++;
			
			if(firstElmt == null){
				firstElmt = sameCategoryInMap;
			}
			
			console.log("validateCatChoices - found brother, " +
					"set them both as invalid, " +
					"nbInvalidPairs is now: " + nbInvalidPairs + ".");
		} else {
			mapCat[currentValue] = currentCatChoice;

			console.log("validateCatChoices - no brother found, " +
					"putting it in map.");
		}
	});
	

	var listFathers = $(".catChoiceFather");
	listFathers.each(function() {

		var currentCatChoice = $(this);
		setValid(currentCatChoice);
		var currentValue = getActualValueFather(currentCatChoice);
		
		console.log("validateCatChoices - father with value: '" + currentValue + "'...");
		// check if a son is already in the map
		var foundSon = false;
		// loop on keys
		for(var key in mapCat){
			var debugMsg = "false";
			if(mapCat.hasOwnProperty(key)){
				// found only if ends with ':'
				// so as to allow both 'Virements' and
				// 'Virements internes' as 2 different
				// categorys
				var sonStart = currentValue + ":";
				if(key.startsWith(sonStart)){
					foundSon = true;
					debugMsg = "true";
				}
				console.log("validateCatChoices - testing VS '" + key + "': " +
						"was estimated to be a son = " + debugMsg + ".");
			}
		}
		
		if(!foundSon){
			console.log("validateCatChoices - found no sons, seeing if already in map...");
			if(mapCat[currentValue]){
				var sameCategoryInMap = mapCat[currentValue];
				setInvalid(currentCatChoice);
				setInvalid(sameCategoryInMap);
				nbInvalidPairs++;
				
				if(firstElmt == null){
					firstElmt = sameCategoryInMap;
				}
				
				console.log("validateCatChoices - found brother, " +
						"set them both as invalid, " +
						"nbInvalidPairs is now: " + nbInvalidPairs + ".");
			} else {
				mapCat[currentValue] = currentCatChoice;
				
				console.log("validateCatChoices - no brother found, " +
				"putting it in map.");
			}
		}
		
	});
	
	console.log("validateCatChoices - " + nbInvalidPairs + " invalid pair(s) after both loops.");
	
	var saveButton = PF("saveButton");
	var errorMessageSing = $("#infoNoCatChoiceDuplicateSingularMsg");
	var errorMessagePlur = $("#infoNoCatChoiceDuplicatePluralMsg");
	errorMessageSing.hide();
	errorMessagePlur.hide();
	saveButton.enable();
	
	
	if(nbInvalidPairs > 0){
		var duplicateCatChoiceElmt = errorMessagePlur;
		if(nbInvalidPairs === 1){
			duplicateCatChoiceElmt = errorMessageSing;
		}		
		
		var msgTop = firstElmt.offset().top; // top is same as first catChoice's top
		var categoryZone = $(".categoryZone"); // left is right of categoryZone
		
		var pos = categoryZone.position();
		var msgLeft = categoryZone.offset().left + categoryZone.outerWidth();
		duplicateCatChoiceElmt.css({'top': msgTop + "px", 'left': msgLeft + "px"});
		duplicateCatChoiceElmt.show();
		
		saveButton.disable();
	}
	
	console.log("validateCatChoices - End.");
}
