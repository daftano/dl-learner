<?php
	include('helper_functions.php');
	
	session_start();	

	$subject=$_POST['label'];
	$fromCache=$_POST['cache'];
		
	if (isset($_SESSION['articles'])) $articles=$_SESSION['articles'];
	$id=$_SESSION['id'];
	$ksID=$_SESSION['ksID'];
	
	//write last action into session
	if (strpos($subject,"http://dbpedia.org/resource/")===0) $actionuri=substr (strrchr ($subject, "/"), 1);
	else $actionuri=urlencode($subject);
	$_SESSION['lastAction']='showArticle/'.$actionuri;
	session_write_close();
	setRunning($id,"true");
	
	//get first Letter of label big
	$uri=subjectToURI($subject);
	
	//if article is in session, get it out of the session
	if (isset($articles)){
		foreach ($articles as $key => $value)
		{
			if ($value['uri']==$uri){
				$fromCache=$key;
				break;
			}
		}
	}
	
	//initialize the content variables
	$content="";
	$lastArticles="";
	$artTitle="";
	$lat="";
	$long="";
			
	//get the article
	//if $fromCache is -1, everything is normal
	//if $fromCache is >=0, the article is taken out of the cache
	if ($fromCache<0) {
		//if there are errors see catch block
		try{
			require_once("DLLearnerConnection.php");
			$sc=new DLLearnerConnection($id,$ksID);
			$triples=$sc->getTriples($uri);
			
			//BUILD ARTICLE			
			// goal: display the data in a nice (DBpedia specific way), maybe similar to
			// dbpedia.org/search
			
			//BUILD ARTICLE TITLE
			if (strlen($triples['http://www.w3.org/2000/01/rdf-schema#label'][0]['value'])>0)
				$artTitle=$triples['http://www.w3.org/2000/01/rdf-schema#label'][0]['value'];
			else
				$artTitle=urldecode(str_replace("_"," ",substr (strrchr ($url, "/"), 1)));
			
			// display a picture if there is one
			if (isset($triples['http://dbpedia.org/property/imageCaption'])&&$triples['http://dbpedia.org/property/imageCaption'][0]['type']!='uri') $alt=$triples['http://dbpedia.org/property/imageCaption'][0]['value'];
			else $alt='Picture of '.$artTitle;
			if(isset($triples['http://xmlns.com/foaf/0.1/depiction']))
				$content.='<img src="'.$triples['http://xmlns.com/foaf/0.1/depiction'][0]['value'].'" alt="'.$alt.'" style="float:right; max-width:200px;" \>';
			
			//display where it was redirected from, if it was redirected
			$redirect="";
			if (isset($triples['http://dbpedia.org/property/redirect'])){
				$content.="<span id=\"redirectedFrom\">redirected from '$subject'</span>";
				$redirect=$triples['http://dbpedia.org/property/redirect'][0]['value'];
			}
			
			// add short description in english
			$content.="<h4>Short Description</h4><p>".urldecode($triples['http://dbpedia.org/property/abstract'][0]['value'])."</p>";
				
			// give the link to the corresponding Wikipedia article
			if(isset($triples['http://xmlns.com/foaf/0.1/page']))
				$content .= '<p><img src="images/wikipedia_favicon.png" alt="Wikipedia" style="max-width:20px;" /> <a href="#" onclick="window.open(\''.getPrintableURL($triples['http://xmlns.com/foaf/0.1/page'][0]['value']).'\',\'Wikiwindow\',\'width=800,height=500,top=50,left=50,scrollbars=yes\');">view Wikipedia article</a>, '; 
			$content .= '<img src="images/dbpedia-favicon.ico" alt="DBpedia" style="max-width:20px;"/> <a href="#" onclick="window.open(\''.$uri.'\',\'Wikiwindow\',\'width=800,height=500,top=50,left=50,scrollbars=yes\');">view DBpedia resource description</a>';
			//display photo collection, if there is one
			if (isset($triples['http://dbpedia.org/property/hasPhotoCollection'])){
				$content.=', <img src="images/flickr.png" alt="Flickr" style="max-width:20px;" /> <a href="#" onclick="window.open(\''.$triples['http://dbpedia.org/property/hasPhotoCollection'][0]['value'].'\',\'Wikiwindow\',\'width=800,height=500,top=50,left=50,scrollbars=yes\');">view photo collection</a></p>';
			}
			
			if (isset($triples['http://www.w3.org/2002/07/owl#sameAs'])){
				$content.='<br/><hr><h4>Same as</h4><br/>';
				$content.='<ul>';
				foreach ($triples['http://www.w3.org/2002/07/owl#sameAs'] as $same){
					if ($same['type']=="uri")
						$content .= '<li><a href="'.$same['value'].'" target="_blank">'.urldecode($same['value']).'</a></li>';
					else $content.= '<li>'.urldecode($same['value']).'</li>';
				}
				$content.='</ul>';
			}
			
			$content.='<br/><hr><h4>YAGO Classes</h4><br/>';
			
			// display a list of classes
			if(isset($triples['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']))
				$content .= '<p>'.formatClassArray($triples['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']).'</p>';

			//skos-subjects
			//not used, because one class systems, YAGO, is enough
			/*if (isset($triples['http://www.w3.org/2004/02/skos/core#subject'])){
				$content .= '<br/><p>Skos categories: <ul>';
				foreach($triples['http://www.w3.org/2004/02/skos/core#subject'] as $skos)
					$content .= '<li><a href="'.$skos['value'].'">'.$skos['value'].'</a></li>';
				$content .= '</ul></p>';			
			}*/
			
			//references not used at the moment because some urls cause problems with
			//the xml parser that recognizes html entities, where no entities are
			/*if(isset($triples['http://dbpedia.org/property/reference'])) {
				$content .= '<p>references: <ul>';
				foreach($triples['http://dbpedia.org/property/reference'] as $reference)
					$content .= '<li><a href="'.$reference['value'].'">'.$reference['value'].'</a></li>';
				$content .= '</ul></p>';
			}*/
			
			//display a Google Map if Geo-koordinates are available
			if (isset($triples['http://www.w3.org/2003/01/geo/wgs84_pos#long'])&&isset($triples['http://www.w3.org/2003/01/geo/wgs84_pos#lat'])){
				$content.='<br/><hr><h4>Map of the location</h4><br/>';
				$content.='<div id="map" style="width: 500px; height: 300px;margin-left:30px;"></div>';
				$lat=$triples['http://www.w3.org/2003/01/geo/wgs84_pos#lat'][0]['value'];
				$long=$triples['http://www.w3.org/2003/01/geo/wgs84_pos#long'][0]['value'];
			}
			else{
				$lat="";
				$long="";
			}
			
			$characteristic=array();
			//display only one birthdate
			$birthdates=array("http://dbpedia.org/property/dateOfBirth","http://dbpedia.org/property/birth","http://dbpedia.org/property/birthdate","http://dbpedia.org/property/birthDate");
			$birthdate=false;
			$birthuri=false;
			foreach ($birthdates as $dates){
				if ($birthdate!=false){
					unset($triples[$dates]);
					continue; 
				}
				if (isset($triples[$dates])&&$triples[$dates][0]['type']!='uri'){
					$birthdate=$dates;
					$characteristics['Birthdate']=$triples[$dates];	
				}
				else if ($triples[$dates][0]['type']=='uri'&&$birthuri==false){
					$birthuri=$dates;
					$characteristics['Birthdate']=$triples[$dates];
				}
				else unset($triples[$dates]);
			}
			
			//display only one deathdate
			$deathdates=array("http://dbpedia.org/property/death","http://dbpedia.org/property/dateOfDeath","http://dbpedia.org/property/deathdate","http://dbpedia.org/property/deathDate");
			$deathdate=false;
			$deathuri=false;
			foreach ($deathdates as $dates){
				if ($deathdate!=false){
					unset($triples[$dates]);
					continue; 
				}
				if (isset($triples[$dates])&&$triples[$dates][0]['type']!='uri'){
					$deathdate=$dates;
					$characteristics['Deathdate']=$triples[$dates];
				}
				else if ($triples[$dates][0]['type']=='uri'&&$deathuri==false){
					$deathuri=$dates;
					$characteristics['Deathdate']=$triples[$dates];
				}
				else unset($triples[$dates]);
			}
			
			//display a small characteristics of a person, if at least the birth date is given 
			if ($birthdate!=false||$birthuri!=false){
				$content.='<br/><hr><h4>Characteristics</h4><br/>';
				
				if (isset($triples['http://dbpedia.org/property/birthname'])) $characteristics['Birthname']=$triples['http://dbpedia.org/property/birthname'];
				if (isset($triples['http://dbpedia.org/property/birthPlace'])) $characteristics['Birthplace']=$triples['http://dbpedia.org/property/birthPlace'];
				if (isset($triples['http://dbpedia.org/property/deathPlace'])) $characteristics['Deathplace']=$triples['http://dbpedia.org/property/deathPlace'];
				if (isset($triples['http://dbpedia.org/property/spouse'])) $characteristics['Spouse']=$triples['http://dbpedia.org/property/spouse'];
				if (isset($triples['http://dbpedia.org/property/shortDescription'])) $characteristics['Short Description']=$triples['http://dbpedia.org/property/shortDescription'];
				if (isset($triples['http://dbpedia.org/property/alternativeNames'])) $characteristics['Alternative Names']=$triples['http://dbpedia.org/property/alternativeNames'];
				$content.=createCharacteristics($characteristics);
				unset($triples['http://dbpedia.org/property/birthname']);
				unset($triples['http://dbpedia.org/property/birthPlace']);
				unset($triples['http://dbpedia.org/property/deathPlace']);
				unset($triples['http://dbpedia.org/property/spouse']);
				unset($triples['http://dbpedia.org/property/shortDescription']);
				unset($triples['http://dbpedia.org/property/alternativeNames']);
				if ($birthdate!=false) unset($triples[$birthdate]);
				if ($birthuri!=false) unset($triples[$birthuri]);
				if ($deathdate!=false) unset($triples[$deathdate]);
				if ($deathuri!=false) unset($triples[$deathuri]);
			}
									
			
			//display foreign wiki pages
			$languages=array('Deutsch'=>'http://dbpedia.org/property/wikipage-de'
							,'Espa%C3%B1ol'=>'http://dbpedia.org/property/wikipage-es'
							,'suomi'=>'http://dbpedia.org/property/wikipage-fi'
							,'Fran%C3%A7ais'=>'http://dbpedia.org/property/wikipage-fr'
							,'Italiano'=>'http://dbpedia.org/property/wikipage-it'
							,'%E6%97%A5%E6%9C%AC%E8%AA%9E'=>'http://dbpedia.org/property/wikipage-ja'
							,'Nederlands'=>'http://dbpedia.org/property/wikipage-nl'
							,'%E2%80%AANorsk (bokm%C3%A5l)'=>'http://dbpedia.org/property/wikipage-no'
							,'Polski'=>'http://dbpedia.org/property/wikipage-pl'
							,'Portugu%C3%AAs'=>'http://dbpedia.org/property/wikipage-pt'
							,'%D0%A0%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9'=>'http://dbpedia.org/property/wikipage-ru'
							,'Svenska'=>'http://dbpedia.org/property/wikipage-sv'
							,'%E4%B8%AD%E6%96%87'=>'http://dbpedia.org/property/wikipage-zh');
			
			$pages="";
			foreach ($languages as $key=>$value){
				if (isset($triples[$value])){
					$pages.='<a href="'.$triples[$value][0]['value'].'" target="_blank">'.urldecode($key).'</a>, ';
					unset($triples[$value]);
				}				
			}
			if (strlen($pages)>0){
				$pages=substr($pages,0,strlen($pages)-2);
				$content.='<br/><hr><h4>Wikipedia articles in different languages</h4><br/><p>'.$pages.'</p>';
			}
			
			// filter out uninteresting properties and properties which
			// have already been displayed
			unset($triples['http://www.w3.org/2002/07/owl#sameAs']);
			unset($triples['http://xmlns.com/foaf/0.1/page']);
			unset($triples['http://xmlns.com/foaf/0.1/depiction']);
			unset($triples['http://dbpedia.org/property/abstract']);
			unset($triples['http://www.w3.org/1999/02/22-rdf-syntax-ns#type']);
			unset($triples['http://dbpedia.org/property/redirect']);
			unset($triples['http://dbpedia.org/property/reference']);
			unset($triples['http://www.w3.org/2003/01/geo/wgs84_pos#long']);
			unset($triples['http://www.w3.org/2003/01/geo/wgs84_pos#lat']);
			unset($triples['http://dbpedia.org/property/hasPhotoCollection']);
			unset($triples['http://www.w3.org/2004/02/skos/core#subject']);
			unset($triples['http://www.w3.org/2000/01/rdf-schema#label']);
			unset($triples['http://www.w3.org/2000/01/rdf-schema#comment']);
			unset($triples['http://dbpedia.org/property/latSec']);
			unset($triples['http://dbpedia.org/property/lonSec']);
			unset($triples['http://dbpedia.org/property/lonDeg']);
			unset($triples['http://dbpedia.org/property/latMin']);
			unset($triples['http://dbpedia.org/property/lonMin']);
			unset($triples['http://dbpedia.org/property/latDeg']);
			unset($triples['http://dbpedia.org/property/lonMin']);
			unset($triples['http://www.georss.org/georss/point']);
			unset($triples['http://dbpedia.org/property/audioProperty']);
			unset($triples['http://dbpedia.org/property/wikiPageUsesTemplate']);
			unset($triples['http://dbpedia.org/property/relatedInstance']);
			unset($triples['http://dbpedia.org/property/boxWidth']);
			unset($triples['http://dbpedia.org/property/pp']);
			unset($triples['http://dbpedia.org/property/caption']);
			unset($triples['http://dbpedia.org/property/s']);
			unset($triples['http://dbpedia.org/property/lifetimeProperty']);
			unset($triples['http://dbpedia.org/property/imagesize']);
			unset($triples['http://dbpedia.org/property/id']);
			unset($triples['http://dbpedia.org/property/issue']);
			if ($triples['http://dbpedia.org/property/hips'][0]['type']=='uri') unset($triples['http://dbpedia.org/property/hips']);
			if ($triples['http://dbpedia.org/property/weight'][0]['type']=='uri') unset($triples['http://dbpedia.org/property/weight']);
			if ($triples['http://dbpedia.org/property/waist'][0]['type']=='uri') unset($triples['http://dbpedia.org/property/waist']);
			if ($triples['http://dbpedia.org/property/height'][0]['type']=='uri') unset($triples['http://dbpedia.org/property/height']);
			unset($triples['http://www.geonames.org/ontology#featureCode']);
			unset($triples['http://www.geonames.org/ontology#featureClass']);
			unset($triples['http://dbpedia.org/property/dmozProperty']);
			unset($triples['http://dbpedia.org/property/color']);
			unset($triples['http://dbpedia.org/property/imageCaption']);
			unset($triples['http://dbpedia.org/property/name']);
			
			if (count($triples)>0){
				$content.='<br/><hr><h4>Remaining Triples</h4><br/>';
				
				// display the remaining properties as list which can be used for further navigation
				$content .= get_triple_table($triples);
			}
			
			//Restart the Session
			session_start();
			
			//store article in session, to navigate between last 5 articles quickly
			$contentArray=array('content' => $content,'subject' => $artTitle,'uri' => $uri);
			if (!isset($_SESSION['nextArticle'])){
				$_SESSION['nextArticle']=0;
				$_SESSION['articles']=array();
			}
			if ($_SESSION['nextArticle']==5) $_SESSION['nextArticle']=0;
			$_SESSION['articles'][$_SESSION['nextArticle']]=$contentArray;
			$_SESSION['currentArticle']=$_SESSION['nextArticle'];
			$_SESSION['nextArticle']++;
				
			//Add Positives to Session
			if (!isset($_SESSION['positive'])){
				if ($redirect!=""){
					$array=array($redirect => $artTitle);
				}
				else $array=array($uri => $artTitle);
				$_SESSION['positive']=$array;
			}
			else{
				$array=$_SESSION['positive'];
				if ($redirect!="") $array[$redirect] = $artTitle;
				else $array[$uri]=$artTitle;
				$_SESSION['positive']=$array;
			}
									
		} catch (Exception $e)
		{
			$content=$e->getMessage();
			$artTitle="Article not found";
		}
	}
	else {
		session_start();
		//Article is in session
		$content=$_SESSION['articles'][$fromCache]['content'];
		$artTitle=$_SESSION['articles'][$fromCache]['subject'];
	}
	
	//Build lastArticles
	if (isset($_SESSION['articles'])){
		foreach ($_SESSION['articles'] as $key => $value)
		{
			$lastArticles.="<a href=\"\" onclick=\"get_article('label=&cache=".$key."');return false;\">".$value['subject']."</a><br/>";
		}
	}
	
	//add Positives and Negatives to Interests
	$interests=show_Interests($_SESSION);
		
	print $content;
	print '$$$';
	print $artTitle;
	print '$$$';
	print $lastArticles;
	print '$$$';
	print $interests[0];
	print '$$$';
	print $interests[1];
	print '$$$';
	print $lat;
	print '$$$';
	print $long;
?>