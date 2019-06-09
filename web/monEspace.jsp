<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>

<%@include file="header.jsp" %>
<body>
<%@include file="navigation.jsp"%>

	<div class="jumbotron text-center">
		<h1>Bienvenue sur votre espace, <c:out value="${sessionScope.nomUtilisateur}"> </c:out> </h1>
	</div>

	<div class="container">
		<a class="btn btn-secondary" href="index.jsp" role="button"><span class="glyphicon glyphicon-menu-left"></span> Retour accueil</a>
		<h2>Mes réservations</h2>
		<div class="container">
			<table class="table table-hover">
				<tr>
					<th class="col-md-2">Véhicule rfid</th>
					<th class="col-md-2">Catégorie véhicule</th>
					<th class="col-md-2">Modèle véhicule</th>
					<th class="col-md-2">Date de reservation</th>
					<th class="col-md-2">Date de rendue</th>
				</tr>
				<c:forEach items="${mesReservations}" var="item">
					<tr>
						<td>${item.vehicule.rfid}</td>
						<td>${item.vehicule.typeVehicule.categorie}</td>
						<td>${item.vehicule.typeVehicule.typeVehicule}</td>
						<td>${item.dateReservation}</td>
						<td>${item.dateEcheance}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
    </div>

	<div class="container">
		<h2>Mes utilisations</h2>
		<div class="container">
			<table class="table table-hover">
				<tr>
					<th class="col-md-2">Véhicule</th>
					<th class="col-md-2">Date</th>
					<th class="col-md-3">Borne de départ</th>
					<th class="col-md-3">Borne d'arrivée</th>
				</tr>

				<c:forEach items="${mesUtilisations}" var="item">
					<tr>
						<td>${item.vehicule.typeVehicule.typeVehicule}</td>
						<td>${item.date}</td>
						<td>${item.borneDepart.station.numero} ${item.borneDepart.station.adresse},<br />${item.borneDepart.station.codePostal} ${item.borneDepart.station.ville}</td>
						<td>${item.borneArrivee.station.numero} ${item.borneArrivee.station.adresse},<br />${item.borneArrivee.station.codePostal} ${item.borneArrivee.station.ville}</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
<%@include file="footer.jsp"%>
</body>
</html>