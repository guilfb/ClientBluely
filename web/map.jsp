<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>

<style>
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
</style>

<%@include file="header.jsp"%>

<body>
<%@include file="navigation.jsp"%>

<c:if test="${sessionScope.id > 0 }">
	<div class="row" style="margin-right: 50px; margin-left: 50px; margin-top: 50px;">

        <div class="col-sm-2">
            <div name="titreDiv" style="color: black;">Catégorie de véhicule</div>
            <div id="formType" style="border: 1px solid lightgrey; border-radius: 5px; margin-bottom: 100px;"></div>
            <div name="titreDiv" style="color: black;">Modèle de véhicule</div>
            <div id="formVehicule" style="border: 1px solid lightgrey; border-radius: 5px;"></div>
        </div>

        <div class="col-sm-8">
            <div id="map" style="width:100%; height:80%;"></div>
        </div>

        <div name="titreDiv" style="color: black;">Afficher les véhicules ou les places disponibles.</div>
        <div class="col-sm-2" style="border: 1px solid lightgrey; border-radius: 5px;">
            <div id="formParking">
                <form>
                    <label class="radio-inline">
                        <input type="radio" id="reservation" name="optradio" checked>Véhicules
                    </label>
                    <label class="radio-inline">
                        <input type="radio" id="parking" name="optradio">Parkings
                    </label>
                </form>
            </div>
        </div>

    </div>
</c:if>
<%@include file="footer.jsp"%>
</body>
</html>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"></script>
<script>

    window.bornes = ${bornes};
    window.alreadyReserved = ${mesResa};
    window.alreadyUsed = ${mesUsed};

    window.stations = [];
    window.vehicules = [];
    window.typeVehicules = [];
    window.categVehicules = [];
    window.categAffiche = [];
    window.vehiculeChoisis = [];
    window.stationsAffiche = [];
    window.marker = [];
    window.nbVehicules = [];
    window.nbPlaces = [];
    window.url = "";
    window.parking = false;

    window.idTemp = 0;

    remplirListes();

    function remplirListes() {
        // Init de toutes les listes
        window.bornes.forEach(function(value) {
            window.stations.push(value.stationBorne);
            window.vehicules.push(value.vehiculeBorne);
        });

        // Liste de stations
        var idDejaVisit = [];
        window.stations = window.stations.filter(function(elem, index, self) {
            if(idDejaVisit.indexOf(elem.idStation) === -1){
                idDejaVisit.push(elem.idStation);
                return true;
            }
            return false;
        });

        // Affichage de la map
        afficherMap();

        // Liste de vehicules
        var idDejaVisit2 = [];
        window.vehicules = window.vehicules.filter(function(elem, index, self) {
            if((idDejaVisit2.indexOf(elem.idVehicule) === -1) && !(elem.idVehicule === "NO_VEHICULE" )){
                idDejaVisit2.push(elem.idVehicule);
                window.typeVehicules.push(elem.typeVehicule);
                return true;
            }
            return false;
        });

        // Liste de typeVehicule
        var idDejaVisit3 = [];
        window.typeVehicules = window.typeVehicules.filter(function(elem, index, self) {
            if(idDejaVisit3.indexOf(elem.idTV) === -1){
                idDejaVisit3.push(elem.idTV);
                idDejaVisit3.push(elem.idTV);
                return true;
            }
            return false;
        });

        // Liste de categVehicule
        var idDejaVisit4 = [];
        window.categVehicules = window.typeVehicules.filter(function(elem, index, self) {
            if (idDejaVisit4.indexOf(elem.categTV) === -1) {
                idDejaVisit4.push(elem.categTV);
                return true;
            }
            return false;
        });

        window.stations.forEach(function(item) {
            window.nbVehicules[item.idStation - 1] = 0;
        });

        window.stations.forEach(function(item) {
            window.nbPlaces[item.idStation - 1] = 0;
        });

        window.stations.forEach(function(item) {
            window.bornes.forEach(function(elem) {
                if(elem.stationBorne.idStation === item.idStation) {
                    if(elem.etatBorne === 0) {
                        window.nbVehicules[item.idStation - 1]++;
                    }else if(elem.etatBorne === 1) {
                        window.nbPlaces[item.idStation - 1]++;
                    }
                }
            })
        });

        remplirCategVehicule();
    };

    function afficherMap() {
        window.map = L.map('map', {
            center: [45.76, 4.835],
            zoom: 13
        });
        var osmLayer = L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 19
        });
        map.addLayer(osmLayer);

        window.myIcon = L.icon({
            iconUrl: '/resources/images/popup.png',
            iconSize: [38, 95],
            iconAnchor: [22, 94],
            popupAnchor: [-3, -76],
        });

    };

    $('#formType').change(function() {
        ifChecked();
        ifCheckedVehicule();
    });

    $('#formVehicule').change(function() {
        ifCheckedVehicule();
    });

    $('#formParking').change(function() {
        window.parking = $("input[type='radio'][id='parking']").prop("checked");

        triMarker();
    });

	function ifChecked() {
	    window.categAffiche = [];
	    window.categVehicules.forEach(function(value) {
            if($("input[type='checkbox'][value='"+value.categTV+"']").prop("checked")) {
                window.categAffiche.push(value.categTV);
            }
        });
        remplirVehicule();
    };

    function ifCheckedVehicule() {
        window.vehiculeChoisis = [];
        window.typeVehicules.forEach(function(value) {
            if($("input[type='checkbox'][value='"+value.TV+"']").prop("checked")) {
                window.vehiculeChoisis.push(value.TV);
            }
        });
        triMarker();
    };

    function remplirCategVehicule() {
        window.categVehicules.forEach(function(value) {
            $("#formType").append("<div class=\"form-check\">\n" +
                "<input class=\"form-check-input\" type=\"checkbox\" name=\"exampleRadios\" id='" + value.categTV + "' value='" + value.categTV + "' checked=\"checked\">\n" +
                "<label class=\"form-check-label\" style=\"color: grey;\" for='"+ value.categTV +"'>" + value.categTV + "</label>\n" +
                "</div>");
        });
        ifChecked();
        ifCheckedVehicule();
    };

    function remplirVehicule() {
        $("#formVehicule").empty();

        window.typeVehicules.forEach(function(item) {
            if(window.categAffiche.indexOf(item.categTV) !== -1){
                $("#formVehicule").append(
                    "<div class=\"form-check\">\n" +
                    "<input class=\"form-check-input\" type=\"checkbox\" name=\"exampleRadios\" id='" + item.TV + "' value='" + item.TV + "' checked=\"checked\">\n" +
                    "<label class=\"form-check-label\" style=\"color: grey;\" for='"+item.TV+"'>" + item.TV + "</label>\n" +
                    "</div>");
            }
        });
    };

	function triMarker() {
	    window.marker.forEach(function(item) {
            map.removeLayer(item);
        });

        window.marker = [];
        window.stationsAffiche = [];
        var marker;

        if(window.alreadyUsed){
            $("input[type='radio'][id='reservation']").prop("checked",false);
            $("input[type='radio'][id='parking']").prop("checked",true);
            window.parking = true;
        }

        if(window.parking === false) {
            window.bornes.forEach(function(elem) {
                if (elem.vehiculeBorne.idVehicule !== "NO_VEHICULE") {
                    if (window.vehiculeChoisis.indexOf(elem.vehiculeBorne.typeVehicule.TV) !== -1) {
                        if (window.stationsAffiche.indexOf(elem.stationBorne.idStation) === -1) {
                            window.stationsAffiche.push(elem.stationBorne.idStation);

                            window.idTemp = elem.stationBorne.idBorne;

                            if(window.alreadyReserved && !window.alreadyUsed){
                                marker = L.marker([elem.stationBorne.latitudeStation, elem.stationBorne.longitudeStation])
                                    .addTo(map)
                                    .bindPopup('<div><p>'
                                        + elem.stationBorne.numAdresseStation + ', '
                                        + elem.stationBorne.adresseStation + '<br />'
                                        + elem.stationBorne.cpStation + ', '
                                        + elem.stationBorne.villeStation + '<br /><br />'
                                        + '<img src="./resources/images/logo_parking.png" alt="Parking Slot" height="50" width="50"> '
                                        + ' ' + window.nbPlaces[elem.stationBorne.idStation -1] + ' <br />'
                                        + '<img src="./resources/images/logo_voiture.png" alt="Vehicule" height="50" width="50"> '
                                        + window.nbVehicules[elem.stationBorne.idStation -1] + '</p>'
                                        + 'Reservation en cours'
                                        + '<button name="retirer" id="'+elem.stationBorne.idStation+'" onclick="makeUrlRetirer('+elem.stationBorne.idStation+');"/>Retirer</button>' +
                                        + '</div>');
                            }else if(window.alreadyUsed) {
                                marker = L.marker([elem.stationBorne.latitudeStation, elem.stationBorne.longitudeStation])
                                    .addTo(map)
                                    .bindPopup('<div><p>'
                                        + elem.stationBorne.numAdresseStation + ', '
                                        + elem.stationBorne.adresseStation + '<br />'
                                        + elem.stationBorne.cpStation + ', '
                                        + elem.stationBorne.villeStation + '<br /><br />'
                                        + '<img src="./resources/images/logo_parking.png" alt="Parking Slot" height="50" width="50"> '
                                        + window.nbPlaces[elem.stationBorne.idStation -1]
                                        + '<img src="./resources/images/logo_voiture.png" alt="Vehicule" height="50" width="50"> '
                                        + window.nbVehicules[elem.stationBorne.idStation -1] + '</p>'
                                        + 'Utilisation en cours'
                                        + '</div>');
                            }else{
                                marker = L.marker([elem.stationBorne.latitudeStation, elem.stationBorne.longitudeStation])
                                    .addTo(map)
                                    .bindPopup('<div><p>'
                                        + elem.stationBorne.numAdresseStation + ', '
                                        + elem.stationBorne.adresseStation + '<br />'
                                        + elem.stationBorne.cpStation + ', '
                                        + elem.stationBorne.villeStation + '<br /><br />'
                                        + '<img src="./resources/images/logo_parking.png" alt="Parking Slot" height="50" width="50"> '
                                        + ' ' + window.nbPlaces[elem.stationBorne.idStation -1]
                                        + '<img src="./resources/images/logo_voiture.png" alt="Vehicule" height="50" width="50"> '
                                        + window.nbVehicules[elem.stationBorne.idStation -1] + '</p>'
                                        + '<button name="reservation" id="'+elem.stationBorne.idStation+'" onclick="makeUrlReserver('+elem.stationBorne.idStation+');"/>Réserver</button>'
                                        + '<button name="retirer" id="'+elem.stationBorne.idStation+'" onclick="makeUrlRetirer('+elem.stationBorne.idStation+');"/>Retirer</button>'
                                        + '</div>');
                            }
                            window.marker.push(marker);
                        }
                    }
                }
            });
        }else{
            window.bornes.forEach(function(elem) {
                if (elem.vehiculeBorne.idVehicule === "NO_VEHICULE") {
                    if (window.stationsAffiche.indexOf(elem.stationBorne.idStation) === -1) {
                        window.stationsAffiche.push(elem.stationBorne.idStation);

                        if(window.alreadyUsed) {
                            marker = L.marker([elem.stationBorne.latitudeStation, elem.stationBorne.longitudeStation])
                                .addTo(map)
                                .bindPopup('<div><p>'
                                    + elem.stationBorne.numAdresseStation + ', '
                                    + elem.stationBorne.adresseStation + '<br />'
                                    + elem.stationBorne.cpStation + ', '
                                    + elem.stationBorne.villeStation + '<br /><br />'
                                    + '<img src="./resources/images/logo_parking.png" alt="Parking Slot" height="50" width="50"> '
                                    + window.nbPlaces[elem.stationBorne.idStation -1]
                                    + '<img src="./resources/images/logo_voiture.png" alt="Vehicule" height="50" width="50"> '
                                    + window.nbVehicules[elem.stationBorne.idStation -1] + '</p>'
                                    + 'Utilisation en cours'
                                    + '<button name="rendre" id="'+elem.stationBorne.idStation+'" onclick="makeUrlRendre('+elem.stationBorne.idStation+');"/>Rendre</button>'
                                    + '</div>');
                        }else{
                            marker = L.marker([elem.stationBorne.latitudeStation, elem.stationBorne.longitudeStation])
                                .addTo(map)
                                .bindPopup('<div><p>'
                                    + elem.stationBorne.numAdresseStation + ', '
                                    + elem.stationBorne.adresseStation + '<br />'
                                    + elem.stationBorne.cpStation + ', '
                                    + elem.stationBorne.villeStation + '<br /><br />'
                                    + '<img src="./resources/images/logo_parking.png" alt="Parking Slot" height="50" width="50"> '
                                    + ' ' + window.nbPlaces[elem.stationBorne.idStation -1] + ' <br />'
                                    + '<img src="./resources/images/logo_voiture.png" alt="Vehicule" height="50" width="50"> '
                                    + window.nbVehicules[elem.stationBorne.idStation -1] + '</p>' );
                        }
                        window.marker.push(marker);
                    }
                }
            });
        }
    };

	function makeUrlReserver(idStation) {
	    var urlTemp = "";
        var i=0;
        window.typeVehicules.forEach(function(elem) {
            window.vehiculeChoisis.forEach(function(item) {
                if(elem.TV === item) {
                    urlTemp+="&id"+i+"="+elem.idTV;
                    i++;
                }
            });
        });

        var url = "Controleur?action=reserverVehicule&idStation=" + idStation + "&idV=" + i + urlTemp;
        window.location.href = url;
    };

	function makeUrlRendre(idStation) {
        var url = "Controleur?action=rendreVehicule&idStation=" + idStation
        window.location.href = url;
    };

	function makeUrlRetirer(idStation) {
        var urlTemp = "";
        var i=0;
        window.typeVehicules.forEach(function(elem) {
            window.vehiculeChoisis.forEach(function(item) {
                if(elem.TV === item) {
                    urlTemp+="&id"+i+"="+elem.idTV;
                    i++;
                }
            });
        });

        var url = "Controleur?action=retirerVehicule&idStation=" + idStation + "&idV=" + i + urlTemp;
        window.location.href = url;
    };

</script>
