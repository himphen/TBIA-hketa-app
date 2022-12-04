//
//  GoogleMapsView.swift
//  iosApp
//
//  Created by Himphen on 2022-12-04.
//  Copyright © 2022 orgName. All rights reserved.
//

import GoogleMaps
import SwiftUI
import shared

struct GoogleMapsView: UIViewRepresentable {
    
    @Binding var markers: [GMSMarker]
    @Binding var bounds: GMSCoordinateBounds?
    @Binding var coordianteForZooming: CLLocationCoordinate2D?
    
    @State var isSetBounds = false
    @State var isSetMarkers = false
    
    let gmsMapView = GMSMapView(frame: .zero)
    
    var mapMarkerClicked: (RouteDetailsMarkerItem) -> ()
     
     func makeUIView(context: Context) -> GMSMapView {
         let camera = GMSCameraPosition.hongKong
         
         gmsMapView.delegate = context.coordinator
         gmsMapView.camera = camera
         gmsMapView.setMinZoom(14, maxZoom: 20)
         return gmsMapView
     }
     
    func updateUIView(_ uiView: GMSMapView, context: Context) {
        if (!isSetMarkers && !markers.isEmpty) {
            DispatchQueue.main.async {
                isSetMarkers = true
            }
            markers.forEach { marker in
                marker.map = uiView
            }
        }
        
        if (!isSetBounds) {
            if let bounds = bounds {
                if (bounds.isValid) {
                    DispatchQueue.main.async {
                        isSetBounds = true
                    }
                    uiView.cameraTargetBounds = bounds
                    let camera = uiView.camera(for: bounds, insets: UIEdgeInsets())!
                    uiView.camera = camera
                }
            }
        }
        
        if let coordianteForZooming = coordianteForZooming {
            CATransaction.begin()
            CATransaction.setValue(1.5, forKey: kCATransactionAnimationDuration)
            let camera = GMSCameraUpdate.setTarget(coordianteForZooming, zoom: 16)
            uiView.animate(with: camera)
            CATransaction.commit()
            DispatchQueue.main.async {
                self.coordianteForZooming = nil
            }
        }
     }
    
    func makeCoordinator() -> Coordinator {
       Coordinator(owner: self)
    }
    
    class Coordinator: NSObject, GMSMapViewDelegate {
       let owner: GoogleMapsView

       init(owner: GoogleMapsView) {
         self.owner = owner
       }
        
        func mapView(_ mapView: GMSMapView, didTap marker: GMSMarker) -> Bool {
            print("You tapped at \(marker.position.latitude), \(marker.position.longitude)")
            
            if let item = marker.userData as? RouteDetailsMarkerItem {
                self.owner.mapMarkerClicked(item)
                CATransaction.begin()
                CATransaction.setValue(1.5, forKey: kCATransactionAnimationDuration)
                let camera = GMSCameraUpdate.setTarget(marker.position, zoom: 16)
                self.owner.gmsMapView.animate(with: camera)
                CATransaction.commit()
            }
            
            return true
        }

         // ... delegate methods here
    }
 }

extension GMSCameraPosition  {
     static var hongKong = GMSCameraPosition.camera(withLatitude: 22.3112, longitude: 114.1688, zoom: 10)
 }
