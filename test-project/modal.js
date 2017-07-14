

var oldId = 10;
function triggerSelect(clicked_ID){
	var el = document.getElementById(clicked_ID);
    el.style.fontWeight = 'bold';
    if(oldId != 10){
		var p = document.getElementById(oldId);
		p.style.fontWeight = 'normal';
	}
   	oldId = clicked_ID;
};

function onChooseLocation(){
	document.getElementById("chosenFolder").value = oldId;
	document.getElementById("location").innerHTML = oldId;
	var close = document.getElementsByClassName("close")[0];
	modal.style.display = "none";
};

function prepareList() {
 $('#myModal #expList').find('li:has(ul)')
 .click( function(event) {
 if (this == event.target) {
	 	 $(this).toggleClass('collapsed');
		$(this).toggleClass('expanded');
	 	$(this).children('ul').toggle('medium');
 }
 return false;
 })
 .addClass('collapsed')
 .children('ul').hide();

//Hack to add links inside the cv
 $('#expList a').unbind('click').click(function() {
	 window.open($(this).attr('href'));
	 return false;
 });

//Create the button funtionality
 $('#expandList')
 .unbind('click')
 .click( function() {
 	$('.collapsed').children('ul').show('medium');
 })
 	$('#collapseList')
 .unbind('click')
 .click( function() {
	$('.expanded').children('ul').hide('medium');
 })

};


/**************************************************************/
 /* Functions to execute on loading the document               */
 /**************************************************************/
 $(document).ready( function() {
 	prepareList()
 });