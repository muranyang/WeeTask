
$(document).ready(function(){

	$('#submit').on('click', function(event){
		
		window.navigate("home.jsp");
		
		/*
		$(function() {
		    var tooltips = $( "[title]" ).tooltip();
		    $( "<button>" )
		      .text( "Show help" )
		      .button()
		      .click(function() {
		        tooltips.tooltip( "open" );
		      })
		      .insertAfter( "form" );
		  });*/
		
		 /* var fileref=document.createElement("link");
		  fileref.setAttribute("rel", "stylesheet");
		  fileref.setAttribute("type", "text/css");
		  fileref.setAttribute("href", "http://code.jquery.com/ui/1.10.0/themes/eggplant/jquery-ui.css");*/

		  
		//verifying task name

		/*var taskName=$('#taskName').val();
		var  taskNameRegex = /^([A-Za-z ])*$/;
		if(!taskNameRegex.test(taskName)){
			//	$('#taskName').popBox({width:500});
			alert("task name can not have special characters");
			return false;
			//event.preventDefault();
		}*/

		//assignee - only allows letters and spaces, no special characters
		/*var taskAssignee = $('#assignee').val(); 
		var assigneeRegex = /^([A-Za-z ])*$/;
		if( !(assigneeRegex.test(taskAssignee))){

			alert("assignee can not have special characters");
			return false;
			//event.preventDefault();
		}*/
		//verify description
		/*var taskDescription=$('#taskDescription').val();
		var taskDescriptionRegex= /^([A-Za-z _0-9])*$/;
		if(!taskDescriptionRegex.test(taskDescription)){
			alert("description can not have special characters");
			return false;
			//event.preventDefault();
		}*/
		
		//verify date 
		/*var date_regex = /^\d{2}(\/){1}\d{2}(\/)\d{4}$/;   // DD/MM/YYYY
		var dateval = $('#datepicker').val();

		//accept if date box is empty
		if(dateval==""){}
		else if (!date_regex.test(dateval)) {
			alert(dateval);
			alert("wrong date format, must be in format DD/MM/YYYY");
			return false;
			//event.preventDefault();
		} */
			
	});	

	//$('#datepicker').inputmask("mm/dd/yyyy");
	
	  $( document ).tooltip({
        // track: true,
		  show: {
		        effect: "slideDown",
		        delay: 250
		      },
         position:{my: "left+15 center", at: "right center"}
  
	  });
	  //dateMask();
	//currentDate();
	$("#datepicker").datepicker({ 
		//  constrainInput: true,
	      changeMonth: true, 
	      changeYear: true});
	

	//location.reload();
	
	$("input[type=submit], #delete, #update").button().click(
		function(event){
			//event.preventDefault();
		});
	$("#addB").button().click(function(event){
		windows.navigate="home.jsp";
		windows.refresh;
	});
	
	

});


	



/*function currentDate(){
	
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!

	var yyyy = today.getFullYear();
	if(dd<10){dd='0'+dd;}
	if(mm<10){mm='0'+mm;}
	today = mm+'/'+dd+'/'+yyyy;
	
	document.getElementById("datepicker").value = today;
}
*/

