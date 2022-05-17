package ng.gov.imostate

import ng.gov.imostate.database.entity.*
import ng.gov.imostate.model.domain.*

object Mapper {

    private fun mapVehicleToVehicleEntity(vehicle: Vehicle): VehicleEntity {
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

    fun mapTransactionToTransactionEntity(transactionData: TransactionData): TransactionEntity {
        return TransactionEntity(
            transactionData.vehicleId.toString(),
            transactionData.to
        )
    }

    private fun mapTransactionEntityToTransaction(transactionEntity: TransactionEntity): TransactionData {
        return TransactionData(
            transactionEntity.vehicleId,
            transactionEntity.to
        )
    }

    fun mapListOfTransactionToListOfTransactionEntity(transactions: List<TransactionData>): List<TransactionEntity> {
        return transactions.map { transactionData ->
            mapTransactionToTransactionEntity(transactionData)
        }
    }

    fun mapListOfTransactionEntityToListOfTransaction(transactions: List<TransactionEntity>): List<TransactionData> {
        return transactions.map { transactionEntity ->
            mapTransactionEntityToTransaction(transactionEntity)
        }
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

    fun mapRateToRateEntity(rate: Rate): RateEntity {
        return RateEntity(
            rate.id,
            rate.from,
            rate.to,
            rate.status,
            rate.amount,
            rate.createdAt,
            rate.updatedAt,
            rate.category
        )
    }

    fun mapRateEntityToRate(rate: RateEntity): Rate {
        return Rate(
            rate.id,
            rate.from,
            rate.to,
            rate.status,
            rate.amount,
            rate.createdAt,
            rate.updatedAt,
            rate.category
        )
    }

    fun mapListOfRateToListOfRateEntity(rates: List<Rate>): List<RateEntity> {
        return rates.map { rate ->
            mapRateToRateEntity(rate)
        }
    }

    fun mapListOfRateEntityToListOfRate(rates: List<RateEntity>): List<Rate> {
        return rates.map { rate ->
            mapRateEntityToRate(rate)
        }
    }
}