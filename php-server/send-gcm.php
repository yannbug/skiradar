<?php
// Replace with real BROWSER API key from Google APIs
$apiKey = "AIzaSyBMahHBXT7_78s-0psr6l2OgchWsVeHtEo";	//Browser key
//$apiKey = "AIzaSyANSv_pvoL9zwIrmthh9ZEM-q5tacu9OKI";	//Server key: whitelist IPs!!

// Replace with real client registration IDs 
$registrationIDs = array( "APA91bFu6JmSAeRaR-eqEGR96IXcVQ7qqiDE90w8Ohb-Tu8RqWHOWZgnkTuKhsATnIT4kBx22SY5sh-LbkTmCXnnRoz8cyCpjHlo7xbn365tYyQngtgahhIr3WFziQPCMKRsuzljySTTw-Q47AJbwhYlWEqHqo8avw" );

// Message to be sent
$message = "Hello push!";

// Set POST variables
$url = 'https://android.googleapis.com/gcm/send';

$fields = array(
                'registration_ids'  => $registrationIDs,
                'data'              => array( "message" => $message ),
                );

$headers = array( 
                    'Authorization: key=' . $apiKey,
                    'Content-Type: application/json'
                );

// Open connection
$ch = curl_init();

// Set the url, number of POST vars, POST data
curl_setopt( $ch, CURLOPT_URL, $url );

curl_setopt( $ch, CURLOPT_POST, true );
curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );

curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );

// Execute post
$result = curl_exec($ch);

// Close connection
curl_close($ch);

echo $result;
?>
