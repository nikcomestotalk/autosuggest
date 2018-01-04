<?php

$content = file_get_contents("https://www.google.co.in/search?q=php+get+random+sentences+from+online+books&oq=php+get+random+sentences+from+online+books&gs_l=psy-ab.3..33i21k1j33i160k1.2486.15008.0.15054.44.38.1.0.0.0.438.5465.0j17j9j0j1.27.0....0...1.1.64.psy-ab..16.26.5114...0j0i67k1j0i22i30k1j33i22i29i30k1.xm9e-DIbfbk");
$content = preg_replace("/[^A-Za-z0-9 ]/", '', $content);
for($i=0;$i<10000;$i++) {
sendCurl("hello");
}
function sendCurl($query)
{
$query=GeraHash(rand(0,10000))." ".GeraHash(rand(0,10000));
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
  CURLOPT_POSTFIELDS => "{\n\"query\": \"$query\",\n\"filter\": {\n\t\"user\": 12,\n\t\"location\": 12\n}\n}",
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

