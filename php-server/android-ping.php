<?php
list( 
	$user_id,
	$altitude,
	$latitude,
	$longitude,
	$gps_time,
	$friends
) = split( ';', $_SERVER['QUERY_STRING'] );

$connection=mysql_connect("localhost","skiradar","SkiDbUser")
	or die("Impossible de se connecter : " . mysql_error());
mysql_select_db("skiradar");
$strQuery = "
	INSERT INTO
		position_pings (
			user_id,
			altitude,
			latitude,
			longitude,
			gps_time
	) VALUES (
		'$user_id',
		'$altitude',
		'$latitude',
		'$longitude',
		'$gps_time'
	);
";
$sql=mysql_query($strQuery);

/** provisoire pour Debug **/
$file = 'positionlog.log';
$h = fopen( $file, 'a' );
fwrite( $h, $_SERVER['QUERY_STRING'] . "\n" );
fclose( $h );

/** traitement des données amis à retourner **/
$friendsArray = split( ',', $friends );
$result = array();
foreach( $friendsArray as $friend ) {
	$strQuery = "
		SELECT
			user_id,
			altitude,
			latitude,
			longitude,
			gps_time
		FROM
			position_pings
		WHERE
			user_id = '$friend'
		ORDER BY
			id DESC
		LIMIT 1;
	";
	$sql=mysql_query($strQuery);
	while($line=mysql_fetch_array($sql)) {
		$result[$line['user_id']] = array(
			'user_id'	=> $line['user_id'],
			'altitude'	=> $line['altitude'],
			'latitude'	=> $line['latitude'],
			'longitude'	=> $line['longitude'],
			'time'		=> $line['gps_time']
		);
	}
}

header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-type: application/json');
echo json_encode($result);
exit(0);
?>