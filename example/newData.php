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
echo "in here curl \n";
$curl = curl_init();

$arr = [
"query" => $query,
"parameter" => [
"user" => rand(0,10000),
"location" => rand(10,100)
]
];
//$url='http://ec2-54-169-71-183.ap-southeast-1.compute.amazonaws.com:1082/update';
$url='http://127.0.0.1:1082/update';
$json = json_encode($arr,0,23);
$ch = curl_init(); // Initialize cURL
        curl_setopt($ch, CURLOPT_URL,$url);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($ch);
$err = curl_error($ch);

curl_close($ch);
echo "\n $response \n";
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

