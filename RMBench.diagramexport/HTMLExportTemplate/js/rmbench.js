var request = false;
var mouseDown = 0;

var currentWidth = -1;
var currentHeight = -1;

/* size of the window*/
var lastWidth;
var lastHeight;

/* registers the mousemove event to the document
   -- need to this this here so mousemove gets the mouse event -- */
document.onmousemove=mouseMove;
document.onmouseup=mouseReleased;

try {
	request = new XMLHttpRequest();
} catch (tryie) {
	try {
		request = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (otherversion) {
		try {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (failure) {
			request = false;
		}
	}
}

if (!request)
	alert("Error creating request!");
	
window.onresize=initSize;
	
function loadTable(id, name) {
	var url = "tables/table_"+id+".html";
	request.open("GET", url, false);
	request.send(null);
	document.getElementById("tableview").innerHTML = "<h1>"+name+"</h1>"+request.responseText.split("|");
}

function loadDiagram(id, name) {
	var content = 	"<h1>"+name+"</h1>"+
					"<img src='images/diagram_"+id+".jpg' alt='"+name+"'/>";

	document.getElementById("diagramview").innerHTML = content;
}

function verticalMousePressed() {
	mouseDown = 1;
	document.getElementById("verticalSlider").onmouseup=mouseReleased;
}

function horizontalMousePressed() {
	mouseDown = 2;
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
			if (newWidth>(clientWidth-40))
				newWidth=clientWidth-40;
	
			currentWidth=newWidth;
	
			currentWidth = newWidth;
			setElementsWidth(newWidth, "modelview");
			setElementsWidth((clientWidth-30-newWidth), "diagramview");
			
			
			return;
		}
		
		if (mouseDown==2) {
			var clientHeight = getHeight();		
			var newHeight = event.clientY;

			if (newHeight<20)
				newHeight=20;
			if (newHeight>(clientHeight-40))
				newHeight=clientHeight-40;

			currentHeight=newHeight;

			setElementsHeight(newHeight, "modelview");
			setElementsHeight(newHeight, "diagramview");
			setElementsHeight(newHeight, "verticalSlider");

			setElementsHeight(clientHeight-20-currentHeight, "tableview");	
			return;
		}
	}
}

function initSize() {
	if (currentWidth==-1)
		currentWidth=25/100*getWidth();
	else
		currentWidth=currentWidth/lastWidth*getWidth();
		
	if (currentHeight==-1)
		currentHeight=8/10*getHeight();
	else
		currentHeight=currentHeight/lastHeight*getHeight();
		
	lastHeight=getHeight();
	lastWidth=getWidth();	
	
	setElementsWidth(currentWidth, "modelview");
	setElementsWidth(getWidth()-30-currentWidth, "diagramview");
	
	setElementsWidth(getWidth()-20, "tableview");
	setElementsWidth(getWidth()-20, "horizontalSlider");

	setElementsHeight(currentHeight, "modelview");
	setElementsHeight(currentHeight, "diagramview");
	setElementsHeight(currentHeight, "verticalSlider");
	
	setElementsHeight(getHeight()-20-currentHeight, "tableview");
	
	/* if we have a firefox/mozilla/netscape reset cursor icons for slider*/
	if (navigator.appName=="Netscape"){
		document.getElementById("verticalSlider").style.cursor="ew-resize";
		document.getElementById("horizontalSlider").style.cursor="ns-resize";
	}
	
}

function setElementsWidth(width, elementName) {
	document.getElementById(elementName).style.width=width+"px";
}

function setElementsHeight(height, elementName) {
	document.getElementById(elementName).style.height=height+"px";
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

function getHeight() {
  var myHeight = 0;
  if( typeof( window.innerHeight ) == 'number' ) {
    //Non-IE
    myHeight = window.innerHeight;
  } else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
    //IE 6+ in 'standards compliant mode'
    myHeight = document.documentElement.clientHeight;
  } else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
    //IE 4 compatible
    myHeight = document.body.clientHeight;
  }
  return myHeight;
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
