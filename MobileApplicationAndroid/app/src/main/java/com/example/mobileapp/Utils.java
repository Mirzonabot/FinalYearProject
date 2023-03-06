package com.example.mobileapp;

public class Utils {
//    public static GeoCoordinate GetCentralGeoCoordinate(
//            IList<GeoCoordinate> geoCoordinates)
//    {
//        if (geoCoordinates.Count == 1)
//        {
//            return geoCoordinates.Single();
//        }
//
//        double x = 0;
//        double y = 0;
//        double z = 0;
//
//        foreach (var geoCoordinate in geoCoordinates)
//        {
//            var latitude = geoCoordinate.Latitude * Math.PI / 180;
//            var longitude = geoCoordinate.Longitude * Math.PI / 180;
//
//            x += Math.Cos(latitude) * Math.Cos(longitude);
//            y += Math.Cos(latitude) * Math.Sin(longitude);
//            z += Math.Sin(latitude);
//        }
//
//        var total = geoCoordinates.Count;
//
//        x = x / total;
//        y = y / total;
//        z = z / total;
//
//        var centralLongitude = Math.Atan2(y, x);
//        var centralSquareRoot = Math.Sqrt(x * x + y * y);
//        var centralLatitude = Math.Atan2(z, centralSquareRoot);
//
//        return new GeoCoordinate(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI);
//    }
}
