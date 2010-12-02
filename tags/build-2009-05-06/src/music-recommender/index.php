<?php
/**
 * Copyright (C) 2007-2008, Jens Lehmann
 *
 * This file is part of DL-Learner.
 * 
 * DL-Learner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * DL-Learner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

 /**
  * Music recommender index page.
  * 
  * @author Jens Lehmann
  * @author Anita Janassary
  */

// NOTE: We use the xajax-Framework already included in DBpedia-Navigator, so we assume that
// the "dbpedia-navigator" is in the same directory as "music-recommender".

ini_set("soap.wsdl_cache_enabled","0");
// due to bugs in Java _and_ PHP, we have to download the WSDL and XSD
// files locally (if even this does not work you have to do it by hand);
// whenever the web service changes, you have to delete those files
if(!file_exists('main.wsdl')) {
	include('../php-examples/Utilities.php');
	$wsdluri="http://localhost:8181/services?wsdl";
	Utilities::loadWSDLfiles($wsdluri);
}

// $client = new SoapClient('main.wsdl');
// $build = $client->getBuild();
// echo $build;

require_once 'ajax.php';

// doSearch('Allison Crowe');

echo '<?xml version="1.0" encoding="UTF-8"?>';
?>
<!DOCTYPE html 
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<title>DL-Learner Music Recommender</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
		<?php $xajax->printJavascript('../music-recommender/xajax/'); ?>
		<script type="text/javascript" src="http://mediaplayer.yahoo.com/js"></script>
		<style type="text/css">
		<!-- 
		body { height:100%;color: #000; background: #bff2bf; padding: 10px; margin: 1px; text-align: center;  }
		#a {padding:10px;margin:10px;height:86%;background: #dff9df;}
		#b {padding:10px;margin:10px;height:86%;}
		#c {padding:10px;margin:10px;height:86%;background: #669966;}

		//--></style>

	</head>
	<body>
	<div id="a">
		<h1>Music Recommender</h1> 
	</div>

	<div id="b">
		<h2>Search</h2>
		<!-- search form -->
		<input type="text" id="label" />
		<button type="submit" onclick="xajax_doSearch(document.getElementById('label').value);">search</button>
		<p>Enter an artist and click the search button. </p><!--(TODO: Enable search also for tracks and tags.)-->
		<p>Example artist: Allison Crowe</p>
	</div>
		<!-- suche nach Titlen -->
		<div id="c" style="color: #fff;">
		<input type="text" id="label2" />
		<button type="submit" onclick="xajax_doSearchTitle(document.getElementById('label2').value);">Titlen</button>
		<p>Enter a Title and click the Title button. </p>
	</div>

		<!-- search result display -->
	<div id="searchElement" style="max-width:500px;">
	</div>
	 <div id="song">
		<h2>Song List</h2>
		<a href="http://mediaplayer.yahoo.com/example1.mp3">song 1</a> <br />
		<a href="http://mediaplayer.yahoo.com/example2.mp3">song 2</a> <br />
		<a href="http://mediaplayer.yahoo.com/example3.mp3">song 3</a>
	</div> 

	</body>
</html>