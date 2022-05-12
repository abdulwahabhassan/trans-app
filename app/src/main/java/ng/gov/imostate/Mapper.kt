package ng.gov.imostate

import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.RouteEntity
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.model.domain.Driver
import ng.gov.imostate.model.domain.Route
import ng.gov.imostate.model.domain.Vehicle

object Mapper {

    fun mapVehicleToVehicleEntity(vehicle: Vehicle): VehicleEntity {
        return VehicleEntity(
            vehicle.id,
            vehicle.vehiclePlates,
            vehicle.brand,
            vehicle.type,
            vehicle.fleetNumber,
            vehicle.stateOfRegistration,
            vehicle.chasisNumber,
            vehicle.engineNumber,
            vehicle.vehicleModel,
            vehicle.passengerCapacity,
            vehicle.roadWorthinessExpDate,
            vehicle.vehicleLicenceExpDate,
            vehicle.vehicleInsuranceExpDate,
            vehicle.createdAt,
            vehicle.updatedAt,
            vehicle.driver?.let { mapDriverToDriverEntity(it) },
            vehicle.routes?.let { mapListOfRouteToListOfRouteEntity(it) }
        )
    }

    private fun mapDriverToDriverEntity(driver: Driver): DriverEntity {
        return DriverEntity(
            driver.id,
            driver.firstName,
            driver.middleName,
            driver.lastName,
            driver.dOB,
            driver.address,
            driver.email,
            driver.gender,
            driver.maritalStatus,
            driver.bloodGroup,
            driver.meansOfID,
            driver.idNumber,
            driver.stateOfOrigin,
            driver.localGovt,
            driver.bankName,
            driver.accountNumber,
            driver.bvn,
            driver.phone,
            driver.imssin,
            driver.jsonData,
            driver.vehicleID,
            driver.status,
            driver.createdAt,
            driver.updatedAt
        )
    }

    private fun mapRouteToRouteEntity(route: Route): RouteEntity {
        return RouteEntity(
            route.id,
            route.routeID,
            route.vehicleID,
            route.driverID,
            route.status,
            route.createdAt,
            route.updatedAt
        )
    }

    private fun mapListOfRouteToListOfRouteEntity(routes: List<Route>): List<RouteEntity> {
        return routes.map { route ->
            mapRouteToRouteEntity(route)
        }
    }

    fun mapListOfVehicleToListOfVehicleEntity(vehicles: List<Vehicle>): List<VehicleEntity> {
        return vehicles.map { vehicle ->
            mapVehicleToVehicleEntity(vehicle)
        }
    }
}