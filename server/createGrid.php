<?php

$startLat 	= $_REQUEST['start_lat'];
$startLon 	= $_REQUEST['start_lon'];
$endLat 	= $_REQUEST['end_lat'];
$endLon 	= $_REQUEST['end_lon'];
$cellsX		= $_REQUEST['cells_x'];
$cellsY		= $_REQUEST['cells_y'];
$filename	= $_REQUEST['filename'];


$myFile 	= $filename;
$fh 		= fopen($myFile, 'w') or die("can't open file");


$intervalLat = ($endLat - $startLat) / $cellsX;
$intervalLon = ($endLon - $startLon) / $cellsY;


$stringData = '<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://earth.google.com/kml/2.0">
<!-- Processed by GE-Path -->
<Document>
<name>New-Path-Path</name>
<open>1</open>
<Placemark>
   <name>Real Battleship Grid</name>
   <Style>
      <LineStyle>
         <color>afffffff</color>
      </LineStyle>
   </Style>
   <MultiGeometry>';
   
   // VERTICAL LINES
   if ($intervalLon < 0)
   	for ($i = $startLon; $i >= $endLon; $i += $intervalLon)
	   $stringData .= '<LineString>
     <tessellate>1</tessellate>
     <coordinates>
      ' . $i . ',' . $startLat . ',0 ' . $i . ',' . $endLat . ',0
    </coordinates>
   </LineString>';
   else
   	for ($i = $startLon; $i <= $endLon; $i += $intervalLon)
	   $stringData .= '<LineString>
     <tessellate>1</tessellate>
     <coordinates>
      ' . $i . ',' . $startLat . ',0 ' . $i . ',' . $endLat . ',0
    </coordinates>
   </LineString>';
   
   
   
   // HORIZONTAL LINES
   if ($intervalLat < 0)
   	for ($i = $startLat; $i >= $endLat; $i += $intervalLat)
	   $stringData .= '<LineString>
     <tessellate>1</tessellate>
     <coordinates>
      ' . $startLon . ',' . $i . ',0 ' . $endLon . ',' . $i . ',0
    </coordinates>
   </LineString>';
   else
   	for ($i = $startLat; $i <= $endLat; $i += $intervalLat)
	   $stringData .= '<LineString>
     <tessellate>1</tessellate>
     <coordinates>
      ' . $startLon . ',' . $i . ',0 ' . $endLon . ',' . $i . ',0
    </coordinates>
   </LineString>';

   $stringData .= '</MultiGeometry>
</Placemark>
</Document>
</kml>';


fwrite($fh, $stringData);
fclose($fh);

?>