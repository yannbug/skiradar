var posUpdateCounter = 0;
var serverBaseUrl = 'http://www.yann.com/mobile/android-ping.php';
var friendPosMarker = new Array();

function startPositioning() {
	positionLoop();
}

function positionLoop(){
	navigator.geolocation.getCurrentPosition(onPosSuccess, onPosError, { enableHighAccuracy: true });
	setTimeout( 'positionLoop()', 10000 );
}

function onPosSuccess( position ) {
	mapPosition = new OpenLayers.LonLat(position.coords.longitude, position.coords.latitude).transform(fromProjection,toProjection);
	console.log( 'mapPosition: ' + mapPosition );
	map.setCenter( mapPosition );
	//myPosMarker.moveTo( mapPosition );
	layerMarkers.removeMarker( myPosMarker );
	myPosMarker = new OpenLayers.Marker( mapPosition );
	layerMarkers.addMarker( myPosMarker, 'Me', true );
	$("#skiMap").width(window.innerWidth).height(window.innerHeight - $('.toolbar').height());
	map.updateSize();
	posUpdateCounter ++;
	if( posUpdateCounter > 6 ) {
		pingServer( position );
		posUpdateCounter = 0;
	}
}

function onPosError( error ) {
	//Do nothing
	return false;
}

function pingServer( position ) {
	var serverUrl = serverBaseUrl + '?' +
		userId + ';' +
		position.coords.altitude + ';' +
		position.coords.latitude + ';' +
		position.coords.longitude + ';' +
		position.coords.timestamp + ';' +
		$('#friends_list').val();
	console.log( 'pinging server: ' + serverUrl );
	$.get( serverUrl, function( data ){
		data = JSON.parse( data );
		console.log( 'data:' + data );
		if( data instanceof Object ){
			showFriends( data );
		}
	}, 'json');
}

function showFriends( data ) {
	console.log( 'showFriends' );
	var array = $.map(data, function(k, v) {
	    return [k];
	});
	console.log( 'array:' + array );
	var length = array.length;
    var friend = null;
    var friendCount = 0;
	for (var i = 0; i < length; i++) {
		friend = array[i];
		friendCount ++;
		console.log( 'Friend ' + friend['user_id'] );
		if( typeof friendPosMarker !== 'undefined' ) {
			console.log( 'friendPosMarker is defined' );
			if( typeof friendPosMarker[friend['user_id']] !== 'undefined' ) {
				console.log( 'friendPosMarker of friend is defined -> removing' );
				layerMarkers.removeMarker( friendPosMarker[friend['user_id']] );
			}
		} else {
			console.log( 'friendPosMarker is not defined' );
		}
		console.log( 'friend longitude: ' + friend['longitude'] );
		console.log( 'friend latitude: ' + friend['latitude'] );
		friendPosition = new OpenLayers.LonLat(friend['longitude'], friend['latitude']).transform(fromProjection,toProjection);
		console.log( 'friendPosition: ' + friendPosition );
		
		var size = new OpenLayers.Size(32,32);
		var offset = new OpenLayers.Pixel(-(size.w/2), -size.h);
		var iconUrl = 'img/skier' + friendCount + '.png';
		var icon = new OpenLayers.Icon(iconUrl, size, offset);
		console.log( 'iconUrl: ' + iconUrl );
		
		friendPosMarker[friend['user_id']] = new OpenLayers.Marker( friendPosition, icon );
		console.log( 'friendPosMarker created: ' + friendPosMarker[friend['user_id']] );
		layerMarkers.addMarker( friendPosMarker[friend['user_id']], friend['user_id'], true );
	}
}