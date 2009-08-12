var request = false;
var mouseDown = 0;
var isIE = false;

var currentWidth = -1;
var lastWidth;

/* registers the mousemove event to the document
   -- need to this this here so mousemove gets the mouse event -- */
document.onmousemove=mouseMove;
document.onmouseup=mouseReleased;

try {
	request = new XMLHttpRequest();
} catch (tryie) {
	try {
		request = new ActiveXObject("Msxml2.XMLHTTP");
		isIE=true;
	} catch (otherversion) {
		try {
			request = new ActiveXObject("Microsoft.XMLHTTP");
			isIE=true;
		} catch (failure) {
			request = false;
			isIE=false;
		}
	}
}

if (!request)
	alert("Error creating request!");

window.onresize=initSize;

function init() {
	initSize();
}

function initSize() {
	if (currentWidth==-1)
		currentWidth=25/100*getWidth();
	else
		currentWidth=currentWidth/lastWidth*getWidth();
	
	lastWidth=getWidth();

	setElementsWidth(currentWidth, "modelview");
	setElementsWidth(10, "verticalSlider");
	setElementsWidth(getWidth()-30-currentWidth, "detail");
	
	if (navigator.appName=="Netscape")
		document.getElementById("verticalSlider").style.cursor="ew-resize";
}
	
function loadTable(id, name) {
	var url = "tables/table_"+id+".html";
	request.open("GET", url, false);
	request.send(null);
	document.getElementById("detail").innerHTML = "<h1>"+name+"</h1>"+
		request.responseText.split("|");
}

function loadDiagram(id, name) {
	document.getElementById("detail").innerHTML = "<h1>"+name+
		"</h1>"+ "<img src='images/diagram_"+id+".jpg' alt='"+name+"'/>"
}

function verticalMousePressed() {
	mouseDown = 1;
}

function mouseReleased(event) {
	mouseDown = 0;
}

function mouseMove(event) {
	if (mouseDown!=0) {
		if (!event) {
			event = window.event;
		}
		
		if (!event) {
			/* no event given, so we do nothing*/
			return;
		}

		if (mouseDown==1) {
			var clientWidth = getWidth();		
			var newWidth = event.clientX;

			if (newWidth<20)
				newWidth=20;
			if (newWidth>(getWidth()-40))
				newWidth=(getWidth()-40);
	
			currentWidth = newWidth;
			setElementsWidth(newWidth, "modelview");
			setElementsWidth((getWidth()-30-newWidth), "detail");
			
			return;
		}
		
	}
}

function setElementsWidth(width, elementName) {
	document.getElementById(elementName).style.width=width+"px";
}

function getWidth() {
	  var myWidth = 0;
	  if( typeof( window.innerWidth ) == 'number' ) {
	    //Non-IE
	    myWidth = window.innerWidth;
	  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
	    //IE 6+ in 'standards compliant mode'
	    myWidth = document.documentElement.clientWidth;
	  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
	    //IE 4 compatible
	    myWidth = document.body.clientWidth;
	  }
	  return myWidth;
}

function displayNodeChildren(rootId, childClass) {
	var rootNode = document.getElementById(rootId);
	var display = setArrow(rootNode);
	
	var currNode=rootNode.firstChild;

	var currNode;

	var i=1;

	while ((currNode=document.getElementById("n_"+i))) {
		if ((currNode.className) && (currNode.className==childClass)) {
				currNode.style.display=display;
		}
		i++;
	
	}
}

/* Sets the arrow image to the approriate state and returns the new display mode of the child nodes*/
function setArrow(node) {
	var arrow_down = "images/arrow_down.gif";
	var arrow_left = "images/arrow_left.gif";

	currNode=node.firstChild;
	while (currNode.nextSibling) {
		if ((currNode.className) && (currNode.className=="arrow")){
			if (currNode.src.search(arrow_down)!=-1) {
					currNode.src=arrow_left
					return "none";
				}
				else {
					currNode.src=arrow_down;				
					return "block";
				}
		}
		currNode=currNode.nextSibling;
	}
}
