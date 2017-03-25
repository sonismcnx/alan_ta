<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>XML &amp; JSON Data Presentation</title>
		<link rel="stylesheet" href="style.css" />
	</head>
	<body>
		<h1>Analisa Perbandingan Process Time Format Data XML dan JSON</h1>
        <div>
            <ul class="topnav" id="menuTopnav">
                <li><a class="active" href="#" id="homeNavLink">Home</a></li>
                <li><a href="#" id="erdNavLink">ERD</a></li>
                <li class="icon">
                    <a href="javascript:void(0);" onclick="menuFunction()">&#9776;</a>
                </li>
            </ul>
        </div>
        
		<div id="pageBody" >
			<div id="home" class="tabDiv">
				<div id="homePanel">
					<form name="frmQuantity">
						Jumlah&nbsp;record&nbsp;:&nbsp;<select id="qSel" class="qSel">
							<option selected value="a">100</option>
							<option value="b">1.000</option>
							<option value="c">10.000</option>
						</select>
					</form>
					<span class="btn" onclick="fetchResultSet('xml')" >Get XML</span>
					<span class="btn" onclick="fetchResultSet('json')" >Get JSON</span>
					<span class="btn" onclick="getTime()">Get Time</span>
				</div>

				<div id="chartDiv">
					<canvas id="chartCanvas"></canvas>
				</div>

				<div id="resultSet" ></div>
			</div>

			<div id="erd">
				<section>
					<header>
						<h2>Entity Relationship Diagram</h2>
					</header>
					<article>
						<img src="images/data_model.png" id="erdImage" />
				</section>
			</div>
		</div>
			
		<div id="screenOverlay"></div>
			
		<script type="text/javascript" src="node_modules/chart.js/dist/Chart.bundle.js"></script>
		<script type="text/javascript" src="javascript.js"></script>
			
	</body>
</html>