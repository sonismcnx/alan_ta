window.onload = function() {
	document.getElementById("qSel").value="a";
}

/* Toggle between adding and removing the "responsive" class to topnav when the user clicks on the icon */
function menuFunction() {
    var x = document.getElementById("menuTopnav");
    if (x.className === "topnav") {
        x.className += " responsive";
    } else {
        x.className = "topnav";
    }
}

/*var menuTopNavChildren = document.getElementById("menuTopNav").children;*/

var homeDiv = document.getElementById("home");
homeDiv.style.display = "inline-block";
var erdDiv = document.getElementById("erd");
erdDiv.style.display = "none";

var erdNavLink = document.getElementById("erdNavLink");
var homeNavLink = document.getElementById("homeNavLink");

erdNavLink.addEventListener("click", function() {
	var erdDisplay = document.getElementById("erd").style.display;
	if(erdDisplay == "none") {
		homeDiv.style.display = "none";
		erdDiv.style.display = "inline-block";
	}
}, false);

homeNavLink.addEventListener("click", function() {
	var homeDisplay = document.getElementById("home").style.display;
	if(homeDisplay == "none") {
		erdDiv.style.display = "none";
		homeDiv.style.display = "inline-block";
	}
}, false);

function getHTTPObject() {
  var xhr = false;
  if (window.XMLHttpRequest) {
    xhr = new XMLHttpRequest();
  } else if (window.ActiveXObject) {
    try {
      xhr = new ActiveXObject("Msxml2.XMLHTTP");
    } catch(e) {
      try {
        xhr = new ActiveXObject("Microsoft.XMLHTTP");
      } catch(e) {
        xhr = false;
      }
    }
  }
  return xhr;
}

var t0 = 0;
var t1 = 0;
var tx = 0;

function grabFile() {
  var request = getHTTPObject();
  if (request) {
    request.onreadystatechange = function() {
		showPopup(true);
    	displayResponse(request);
		showPopup(false);
    };
    request.open("GET", "images/data_model.png", true);
    request.send(null);
  }
}

var xhrResultSet;

function displayResponse(request, fmt, qLimit) {
	if(request.readyState == 3) { //ready state == 3, downloading data
  		t0 = performance.now();
	}
	if (request.readyState == 4) { //ready state == 4, operation has completed
	  t1 = performance.now();
	    if (request.status == 200) {
	        if(fmt=="json") {
				
	            request.response = "application/json";
	            xhrResultSet = request.responseText;
				tx = round((t1-t0), 4);
				if(qLimit=="b") {
					dataJSON[1] = tx;
				} else if(qLimit=="c"){
					dataJSON[2] = tx;
				} else {
					dataJSON[0] = tx;
				}
				
				console.log(tx + " " + formatSizeUnits(xhrResultSet.length));
				lineChart.update();
				displayJSON(xhrResultSet, qLimit);
				
	        } else if(fmt=="xml") {
				
	            request.response = "text/xml";
	            xhrResultSet = request.responseXML;
				tx = round((t1-t0), 4);
				// var xmlText = request.responseText;
				if(qLimit=="b") {
					dataXML[1] = tx;
				} else if(qLimit=="c"){
					dataXML[2] = tx;
				} else {
					dataXML[0] = tx;
				}
				
				console.log(tx + " " + formatSizeUnits(xmlText.length));
				lineChart.update();
				displayXml(xhrResultSet);
				
	        }
	    }
	}
}

function fetchResultSet(fmt) {
    var request = getHTTPObject();
	qLimit = document.getElementById("qSel").value;
    if(request) {
        request.onreadystatechange = function() {
            displayResponse(request, fmt, qLimit); 
        }
        
        request.open("GET", "GetDataFromDB?srv=sql&fmt=" + fmt + "&qLimit=" + qLimit);
        
        request.send(null);
    }
}

//chart.js stuff
var dataXML = [0, 0, 0];
var dataJSON = [0, 0, 0];
var chartXLabels = ["100", "1000", "10000"]; 
const CHART = document.getElementById("chartCanvas").getContext("2d");
console.log(CHART);
let lineChart = new Chart(CHART, {
    type: 'bar',
    data: {
        	labels: chartXLabels,
        	datasets: [
				{
            		label: 'XML',
            		backgroundColor: "rgba(179,181,198,0.8)",
            		borderColor: "rgba(179,181,198,1)",
            		pointBackgroundColor: "rgba(179,181,198,1)",
            		pointBorderColor: "#fff",
            		pointHoverBackgroundColor: "#fff",
            		pointHoverBorderColor: "rgba(179,181,198,1)",
            		data: dataXML,
					lineTension: 0
        		},
				{
            		label: 'JSON',
            		backgroundColor: "rgba(255,99,132,0.8)",
            		borderColor: "rgba(255,99,132,1)",
            		pointBackgroundColor: "rgba(255,99,132,1)",
            		pointBorderColor: "#fff",
            		pointHoverBackgroundColor: "#fff",
            		pointHoverBorderColor: "rgba(255,99,132,1)",
            		data: dataJSON,
					lineTension: 0
				}
			]
    },
    options: {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true,
					stepSize: 1,
					suggestedMax: 5,
					maxTicksLimit: 1
                },
				scaleLabel : {
					display: true,
					labelString: "Milliseconds"
				}
            }],
			xAxes: [{
				scaleLabel: {
					display: true,
					labelString: "Jumlah Entri",
					fontSize: 12
				}
			}]
        },
		title: {
			display: true,
			text: 'Perbandingan Kecepatan Transfer',
			fontFamily: "sans-serif",
			fontSize: 16
		},
		bezierCurve: false
    }
});

function getTime() {
	var timeNow = new Date();
	alert(timeNow.getMilliseconds());
}

function showPopup(state) {
	var screenOverlay = document.getElementById("screenOverlay");
	if(state==true){
		screenOverlay.style.zIndex = "1";
		screenOverlay.style.visibility = "visible";
	} else {
		screenOverlay.style.zIndex = "0";
		screenOverlay.style.visibility = "hidden";
	}
}

function round(number, decimals) { 
	return +(Math.round(number + "e+" + decimals) + "e-" + decimals); 
}

function formatSizeUnits(bytes) {
    if      (bytes>=1073741824) {bytes=(bytes/1073741824).toFixed(2)+' GB';}
    else if (bytes>=1048576)    {bytes=(bytes/1048576).toFixed(2)+' MB';}
    else if (bytes>=1024)       {bytes=(bytes/1024).toFixed(2)+' KB';}
    else if (bytes>1)           {bytes=bytes+' bytes';}
    else if (bytes==1)          {bytes=bytes+' byte';}
    else                        {bytes='0 byte';}
    return bytes;
}

function displayJSON(jsonObj, quantity) {
	var jsonObj = JSON.parse(jsonObj);
	var colNames = jsonObj.colNames;
	var entries = jsonObj.entries;	
		
	var tableFull = "";
	
	var tableContent = "<tr><th>No</th>";
	
	for(let colName of colNames) {
		tableContent = tableContent + "<th>" + colName + "</th>";
	}
	
	tableContent = tableContent + "</tr>"
	
	
	var rowNum = 1;
	var dataColCount = jsonObj.entries[0].length;
	
	for(i=0; i < jsonObj.entries.length; i++) {

		tableContent = tableContent + "<tr><td>" + rowNum + "</td>";
		
		j = 0;
		while(j < dataColCount) {
			tableContent = tableContent + "<td>" + jsonObj.entries[i][j] + "</td>"
			j++;
		}
		
		tableContent = tableContent + "<tr>"
		rowNum++;
	}
	
	tableFull = "<table border=1>" + tableContent + "</table>"
	
	document.getElementById("resultSet").innerHTML = tableFull;
	
}

function displayXml(xmlData) {
	var nodes = xmlData.querySelectorAll("*");

	for (var i =0; i < nodes.length; i++) {
		
	} 
}