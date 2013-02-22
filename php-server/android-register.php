<?php
$file = 'registration.log';
$h = fopen( $file, 'a' );
fwrite( $h, $_SERVER['QUERY_STRING'] . "\n" );
fclose( $h );
header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-type: application/json');
echo json_encode(1);
exit(0);
?>
