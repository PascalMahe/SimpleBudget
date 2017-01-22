
function warningForCatForm(){
	// warning for replaceCat
	var replacementValue = PF('selectReplacementCat').getSelectedValue();
	if("" !== replacementValue){
		var position = computePositionNextToElement("catForm\\:catReplace");
		showWarning("warningBeforeCatReplace", position.top, position.left);
	}
	
	// warning for setting fatherCat if there already are sonCats
	var fatherValue = PF('selectFatherCat').getSelectedValue();
	var spanContainingSonCatDivs = $("#catForm\\:sonChoiceLine");
	var choosedAFather = fatherValue !== originalFatherValue;

	console.log("warningForCatForm - fatherValue: '" + fatherValue + "'.");
	console.log("warningForCatForm - originalFatherValue: '" + originalFatherValue + "'.");
	console.log("warningForCatForm - choosedAFather: " + choosedAFather + ".");
	var alreadyHadChildren = spanContainingSonCatDivs.length > 0;
	if(choosedAFather && alreadyHadChildren){
		var position = computePositionNextToElement("catForm\\:catFather");
		showWarning("warningBeforeChoosingFatherWhileWithChild", position.top, position.left);
	}
}

function computePositionNextToElement(id){
	var elmtNextTo = $("#" + id);
	var msgTop = elmtNextTo.offset().top; // top is same as first catChoice's top
	
	var msgLeft = elmtNextTo.offset().left + elmtNextTo.outerWidth();
	return {'top': msgTop, 'left': msgLeft};
}

function showWarning(id, msgTop, msgLeft){
	var autoCompleteMsg = $("#" + id);
	
	autoCompleteMsg.css({'top': msgTop + "px", 'left': msgLeft + "px"});
	autoCompleteMsg.show();
}

function hideWarnings(){
	$("#warningBeforeCatReplace").hide();
	$("#warningBeforeChoosingFatherWhileWithChild").hide();
	$(".amountInput").removeClass("autoCompleteTarget warnAutoCompleteTarget infoAutoCompleteTarget");
}
