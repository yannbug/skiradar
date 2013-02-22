map = new OpenLayers.Map("skiMap");
osm = new OpenLayers.Layer.OSM.Mapnik("OpenStreetMap Mapnik");
opm = new OpenLayers.Layer.OSM("OpenPisteMap", "http://tiles.openpistemap.org/nocontours/${z}/${x}/${y}.png", {'numZoomLevels':19,isBaseLayer:false,transparent:true}); 

osm.setIsBaseLayer(true);
opm.setIsBaseLayer(false);
opm.tileOptions.crossOriginKeyword = null;

map.addLayer(new OpenLayers.Layer.OSM());
map.addLayer( opm );

var zoom=15;
var fromProjection = new OpenLayers.Projection("EPSG:4326"); // transform from WGS 1984
var toProjection = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection
var position = new OpenLayers.LonLat(6.4, 45.9).transform(fromProjection,toProjection);
map.setCenter(position, zoom);

var layerMarkers = new OpenLayers.Layer.Markers("Markers");
map.addLayer(layerMarkers);
myPosMarker = new OpenLayers.Marker(position);
layerMarkers.addMarker( myPosMarker, 'Moi', true );