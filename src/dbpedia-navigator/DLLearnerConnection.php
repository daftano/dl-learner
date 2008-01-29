<?php

/**
 * Encapsulates all functions, which require communication with DL-Learner.
 * 
 * @author Jens Lehmann
 * @author Sebastian Knappe
 */
class DLLearnerConnection
{
	private $DBPediaUrl;
	private $ttl;
	private $lang;
	// 
	private $client;
	
	// ID given to this client by the web service
	private $id;
	
	// ID of the DBpedia knowledge source
	private $ksID;
		
	function DLLearnerConnection($id=0,$ksID=0)
	{
		ini_set('default_socket_timeout',200);
		require_once("Settings.php");
		$settings=new Settings();
		$this->ttl=$settings->sparqlttl;
		$this->lang=$settings->language;
		$this->DBPediaUrl=$settings->dbpediauri;
		$this->client=new SoapClient("main.wsdl");
		$this->id=$id;
		$this->ksID=$ksID;
	}
	
	function getIDs()
	{
		$id=$this->client->generateID();
		$ksID=$this->client->addKnowledgeSource($id,"sparql",$this->DBPediaUrl);
		return array(0 => $id, 1 => $ksID);
	}
	
	function getConceptFromExamples($posExamples,$negExamples)
	{
		$this->client->applyConfigEntryInt($this->id, $this->ksID, "recursionDepth",1);
		$this->client->applyConfigEntryStringArray($this->id, $this->ksID, "instances", array_merge($posExamples,$negExamples));
		// $this->client->applyConfigEntryStringArray($this->id, $this->ksID, "predList", array());
		// $this->client->applyConfigEntryStringArray($this->id, $this->ksID, "objList", array());
		// $this->client->applyConfigEntryStringArray($this->id, $this->ksID, "classList", array());
		// $this->client->applyConfigEntryString($this->id, $this->ksID, "format", "KB");
		// $this->client->applyConfigEntryBoolean($this->id, $this->ksID, "dumpToFile", true);
		
		$this->client->setReasoner($this->id, "dig");
		if(empty($negExamples))
			$this->client->setLearningProblem($this->id, "posOnlyDefinition");
		else
			$this->client->setLearningProblem($this->id, "posNegDefinition"); 
		$this->client->setPositiveExamples($this->id, $posExamples);
		if(!empty($negExamples))
			$this->client->setNegativeExamples($this->id, $negExamples);
		$this->client->setLearningAlgorithm($this->id, "refinement");
		
		$start = microtime(true);
		
		$this->client->initAll($this->id);

		$threaded=true;
		
		if($threaded == false) {
	
			$concept = $this->client->learn($this->id);
			
		} else {
		
			$this->client->learnThreaded($this->id);
			
			$i = 1;
			$sleeptime = 1;
			
			do {
				// sleep a while
				sleep($sleeptime);
				
				// see what we have learned so far
				$concept=$this->client->getCurrentlyBestConcept($this->id);
				$running=$this->client->isAlgorithmRunning($this->id);
				
				$seconds = $i * $sleeptime;
				
				$i++;
			} while($seconds<$this->ttl&&$running);
			
			$this->client->stop($this->id);
		}
		return $concept;
	}
			
	function getTriples($label)
	{
		$query="SELECT ?pred ?obj ".
			   "WHERE {<http://dbpedia.org/resource/".str_replace(' ','_',$label)."> ?pred ?obj}";
		$result=$this->getSparqlResult($query);
		$ret=array();
		foreach ($result->item as $results){
			$value=$results->item[1];
			if (strpos($value,"@".$this->lang)==(strlen($value)-strlen("@".$this->lang))) $ret[$results->item[0]][]=substr($value,0,strlen($value)-strlen("@".$this->lang));
			if (strpos($value,"@")!=(strlen($value)-strlen($this->lang)-1)) $ret[$results->item[0]][]=$value;
		}
		return $ret;
	}
	
	function getSparqlResult($query)
	{
		$this->client->applyConfigEntryStringArray($this->id, $this->ksID, "defaultGraphURIs", array("http://dbpedia.org"));
		$queryID=$this->client->sparqlQueryThreaded($this->id,$this->ksID,$query);
		$running=true;
		$i = 1;
		$sleeptime = 1;
		
		do {
			// sleep a while
			sleep($sleeptime);
				
			
			$running=$this->client->isSparqlQueryRunning($this->id,$queryID);
			if (!$running){
				$result=$this->client->getAsStringArray($this->id,$queryID);
				return $result;
			}
			
			$seconds = $i * $sleeptime;
			$i++;
		} while($seconds<$this->ttl);
		$this->client->stopSparqlQuery($id,$queryID);
	}
	
	function getSubjects($label)
	{
		$query="SELECT DISTINCT ?subject\n".
				"WHERE { ?subject <http://www.w3.org/2000/01/rdf-schema#label> ?object. ?object bif:contains '\"".$label."\"'@en}\n".
				"LIMIT 10";
		$result=$this->getSparqlResult($query);
		$ret=array();
		foreach ($result->item as $results){
			$ret[]=$results->item;
		}
		return $ret;
	}
	
	function getSubjectsFromConcept($concept)
	{
		$query="SELECT DISTINCT ?subject\n".
			   "WHERE { ?subject a <".$concept.">}\n".
			   "LIMIT 10";
		$result=$this->getSparqlResult($query);
		$ret=array();
		foreach ($result->item as $results){
			$ret[]=$results->item[0];
		}
		return $ret;
	}
	
	public function loadWSDLfiles($wsdluri){
		$main=DLLearnerConnection::getwsdl($wsdluri);
		$other=DLLearnerConnection::getOtherWSDL($main);
		$newMain=DLLearnerConnection::changeWSDL($main);
		DLLearnerConnection::writeToFile("main.wsdl",$newMain);
		$x=0;
		foreach ($other as $o){
			DLLearnerConnection::writeToFile("def".($x++).".xsd",DLLearnerConnection::getwsdl($o));
		}

	}
	
	private function changeWSDL($wsdl){
		$before="<xsd:import schemaLocation=\"";
		$after="\" namespace=\"";
		$newWSDL="";
		$desca="def";
		$descb=".xsd";
		$x=0;
		while($posstart= strpos ( $wsdl, $before  )){

			$posstart+=strlen($before);
			$newWSDL.=substr($wsdl,0,$posstart);
			$wsdl=substr($wsdl,$posstart);
			$newWSDL.=$desca.($x++).$descb;
			$posend= strpos ( $wsdl, $after  );
			$wsdl=substr($wsdl,$posend);

		}
		return $newWSDL.$wsdl;
			
	}
	
	private function getOtherWSDL($wsdl){
		$before="<xsd:import schemaLocation=\"";
		$after="\" namespace=\"";
		$ret=array();
		while($posstart= strpos ( $wsdl, $before  )){
			$posstart+=strlen($before);
			$wsdl=substr($wsdl,$posstart);
			$posend= strpos ( $wsdl, $after  );
			$tmp=substr($wsdl,0,$posend);
			$ret[]=$tmp;
			$wsdl=substr($wsdl,$posend+strlen($after));
		}
		return $ret;
	}
	

	
	
	private function getwsdl($wsdluri){
		// this is copied from the Pear example
		// please don't ask me how it works
		$req = &new HTTP_Request($wsdluri);
		$message="";
		$req->setMethod(HTTP_REQUEST_METHOD_GET);
		$req->sendRequest();
		$ret=$req->getResponseBody();
		return $ret;
	}
	
	
	
	private function writeToFile($filename,$content){

		$fp=fopen($filename,"w");
		fwrite($fp,$content);
		fclose($fp);
	
	}
}

/*require_once("Settings.php");
require_once("DLLearnerConnection.php");
$settings=new Settings();
$sc=new DLLearnerConnection($settings->dbpediauri,$settings->wsdluri);
$ids=$sc->getIDs();
$sc=new DLLearnerConnection($settings->dbpediauri,$settings->wsdluri,$ids[0],$ids[1]);
$triples=$sc->getTriples($settings->sparqlttl,"dog");*/
?>
