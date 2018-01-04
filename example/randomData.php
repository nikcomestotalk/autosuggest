<?php
$i=0;
while(true)
{
usleep(rand(0,10000));
echo "IN HERE";
$text = file_get_contents("http://writingexercises.co.uk/php/firstline.php?_=1508353732499");
echo "text is $text";
if ($text) {
            $first = str_replace("+"," ",$text);
echo $first. $i++." \n ";
            sendCurl($first);
    }

}
function sendCurl($query)
{
$curl = curl_init();

$arr = [
"query" => $query,
"parameter" => [
"user" => rand(0,10000),
"location" => rand(10,100)
]
];
$url='http://ec2-54-169-71-183.ap-southeast-1.compute.amazonaws.com:1082/update';
$json = json_encode($arr,0,23);
echo $json;
$ch = curl_init(); // Initialize cURL
        curl_setopt($ch, CURLOPT_URL,$url);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $json);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

$response = curl_exec($ch);
$err = curl_error($ch);

curl_close($ch);

  echo $response;
print_r($err);
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

