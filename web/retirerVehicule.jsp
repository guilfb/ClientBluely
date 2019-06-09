<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>

<%@include file="header.jsp" %>
<body>
<%@include file="navigation.jsp"%>

	<div class="jumbotron text-center">
		<h2>Réservation à la station<br/><c:out value="${bornes[0].station.numero}" />, <c:out value="${bornes[0].station.adresse}" /></h2>
	</div>

	<div class="container">
		<a class="btn btn-secondary" href="Controleur?action=map" role="button"><span class="glyphicon glyphicon-menu-left"></span> Retour à la carte</a>
		<div class="container">
			<table class="table table-hover">
				<tr>
					<th class="col-md-1">Véhicule</th>
					<th class="col-md-2">Catégorie Véhicule</th>
					<th class="col-md-2">Modèle Véhicule</th>
					<th class="col-md-1">N° borne</th>
					<th class="col-md-1">Etat batterie</th>
					<th class="col-md-1">Action</th>
				</tr>
				<c:forEach items="${bornes}" var="borne">

				<tr>
					<td><c:out value="${borne.vehicule.idVehicule}" /></td>
					<td><c:out value="${borne.vehicule.typeVehicule.categorie}" /></td>
					<td><c:out value="${borne.vehicule.typeVehicule.typeVehicule}" /></td>
					<td><c:out value="${borne.idBorne}" /></td>
					<td><c:out value="${borne.vehicule.etatBatterie}"/> %</td>

					<c:set var="test" scope="session" value="false" />
					<c:forEach items="${resas}" var="item">
						<c:if test="${item.vehicule.idVehicule == borne.vehicule.idVehicule}">
							<c:set var="test" scope="session" value="true" />
						</c:if>
					</c:forEach>
					<c:choose>
						<c:when test="${test == false}">
							<td><a class="btn btn-info" href="Controleur?action=retirer&idBorne=${borne.idBorne}" role="button"><span
									class="glyphicon glyphicon-pencil"></span> Retirer</a></td>
						</c:when>
						<c:otherwise>
							<td>Cette voiture est reservée</td>
						</c:otherwise>
					</c:choose>

					<c:set var="test1" scope="session" value="false" />
					<c:forEach items="${mesResas}" var="item1">
						<c:if test="${item1.vehicule.idVehicule == borne.vehicule.idVehicule}">
							<c:set var="test1" scope="session" value="true" />
						</c:if>
					</c:forEach>
					<c:choose>
						<c:when test="${test1 == true}">
							<td>Vous avez reservé cette voiture.</td>
						</c:when>
					</c:choose>

				</tr>
				</c:forEach>
			</table>
		</div>
    </div>

<%@include file="footer.jsp"%>
</body>
</html>

<script>
	window.resa = [];
	var i = 0;
	<c:forEach items="${resas}" var="item">
		window.resa[i] = <c:out value="${item.vehicule.idVehicule}" />;
		i++;
	</c:forEach>
</script>