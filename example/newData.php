<?php
$content = file_get_contents("/home/nikhil/Desktop/swdown/suggestmapper/servers/server_1/content/files/file_10.txt");
$file = $argv[1];
#while(true)
if(true)
{
$no =$file;
$handle = fopen("/home/nikhil/Desktop/swdown/suggestmapper/servers/server_1/content/files/file_$no.txt", "r");
if ($handle) {
$i=0;
    while (($line = fgets($handle)) !== false) {
        $first = explode("=",explode("&",$line)[0])[1];
        if($first)
        {
            $first = str_replace("+"," ",$first);
echo $first. $i++." \n ";
            sendCurl($first);
        }
        
    }

    fclose($handle);
} else {
    // error opening the file.
}
} 
//for($i=0;$i<10000;$i++) {
//sendCurl("hello");
//}
function sendCurl($query)
{
//$query=GeraHash(rand(0,10000))." ".GeraHash(rand(0,10000));
$curl = curl_init();


curl_setopt_array($curl, array(
  CURLOPT_PORT => "1082",
  CURLOPT_URL => "http://127.0.0.1:1082/update",
  CURLOPT_RETURNTRANSFER => true,
  CURLOPT_ENCODING => "",
  CURLOPT_MAXREDIRS => 10,
  CURLOPT_TIMEOUT => 30,
  CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
  CURLOPT_CUSTOMREQUEST => "POST",
  CURLOPT_POSTFIELDS => "{\n\"query\": \"$query\",\n\"parameter\": {\n\t\"user\": 12,\n\t\"location\": 12\n}\n}",
  CURLOPT_HTTPHEADER => array(
    "cache-control: no-cache",
    "content-type: application/json",
    "postman-token: ba75d5d7-8b63-560e-2177-989809a986c7"
  ),
));

$response = curl_exec($curl);
$err = curl_error($curl);

curl_close($curl);

  echo $response;
usleep(50000);
}
function GeraHash($qtd){ 
//Under the string $Caracteres you write all the characters you want to be used to randomly generate the code. 
$Caracteres = 'ABCDEFGHIJKLMOPQRSTUVXWYZ0123456789'; 
$QuantidadeCaracteres = strlen($Caracteres); 
$QuantidadeCaracteres--; 

$Hash=NULL; 
    for($x=1;$x<=$qtd;$x++){ 
        $Posicao = rand(0,$QuantidadeCaracteres); 
        $Hash .= substr($Caracteres,$Posicao,1); 
    } 

return $Hash; 
} 

