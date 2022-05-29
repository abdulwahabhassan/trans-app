package ng.gov.imostate

import ng.gov.imostate.database.entity.*
import ng.gov.imostate.model.domain.*

object Mapper {

    private fun mapVehicleToVehiclePreviousEntity(vehicle: Vehicle): VehiclePreviousEntity {
        return VehiclePreviousEntity(
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
            vehicle.vehicleRoutes?.let { mapListOfRouteToListOfRouteEntity(it) }
        )
    }

    fun mapListOfVehicleToListOfVehicleCurrentEntity(vehicles: List<Vehicle>): List<VehicleCurrentEntity> {
        return vehicles.map { vehicle ->
            mapVehicleToVehicleCurrentEntity(vehicle)
        }
    }

    fun mapVehicleCurrentEntityToVehicle(vehicle: VehicleCurrentEntity): Vehicle {
        return Vehicle(
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
            vehicle.lastPaid,
            vehicle.driver?.let { mapDriverEntityToDriver(it) },
            vehicle.driver?.firstName + vehicle.driver?.middleName + vehicle.driver?.lastName,
            vehicle.vehicleRoutes?.let { mapListOfRouteEntityToListOfRoute(it) },
            emptyList()
        )
    }

    private fun mapVehicleToVehicleCurrentEntity(vehicle: Vehicle): VehicleCurrentEntity {
        return VehicleCurrentEntity(
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
            vehicle.lastPaid,
            vehicle.driver?.let { mapDriverToDriverEntity(it) },
            vehicle.vehicleRoutes?.let { mapListOfRouteToListOfRouteEntity(it) }
        )
    }

    fun mapListOfVehicleToListOfVehiclePreviousEntity(vehicles: List<Vehicle>): List<VehiclePreviousEntity> {
        return vehicles.map { vehicle ->
            mapVehicleToVehiclePreviousEntity(vehicle)
        }
    }


    fun mapTransactionToTransactionEntity(transactionData: TransactionData): TransactionEntity {
        return TransactionEntity(
            transactionData.vehicleId.toString(),
            transactionData.to,
            transactionData.amount
        )
    }

    fun mapTransactionEntityToTransaction(transactionEntity: TransactionEntity): TransactionData {
        return TransactionData(
            transactionEntity.vehicleId,
            transactionEntity.to,
            transactionEntity.amount
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
            driver.vehicleId,
            driver.status,
            driver.createdAt,
            driver.updatedAt
        )
    }

    private fun mapDriverEntityToDriver(driver: DriverEntity): Driver {
        return Driver(
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

    private fun mapRouteToRouteEntity(vehicleRoute: VehicleRoute): VehicleRouteEntity {
        return VehicleRouteEntity(
            vehicleRoute.id,
            vehicleRoute.routeID,
            vehicleRoute.from,
            vehicleRoute.to,
            vehicleRoute.vehicleID,
            vehicleRoute.driverID,
            vehicleRoute.status,
            vehicleRoute.createdAt,
            vehicleRoute.updatedAt
        )
    }

    private fun mapRouteEntityToRoute(vehicleRoute: VehicleRouteEntity): VehicleRoute {
        return VehicleRoute(
            vehicleRoute.id,
            vehicleRoute.routeID,
            vehicleRoute.from,
            vehicleRoute.to,
            vehicleRoute.vehicleID,
            vehicleRoute.driverID,
            vehicleRoute.status,
            vehicleRoute.createdAt,
            vehicleRoute.updatedAt
        )
    }

    fun mapListOfRouteEntityToListOfRoute(vehicleRoutes: List<VehicleRouteEntity>): List<VehicleRoute> {
        return vehicleRoutes.map { route ->
            mapRouteEntityToRoute(route)
        }
    }

    fun mapListOfRouteToListOfRouteEntity(vehicleRoutes: List<VehicleRoute>): List<VehicleRouteEntity> {
        return vehicleRoutes.map { route ->
            mapRouteToRouteEntity(route)
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

    private fun mapAgentRouteToAgentRouteEntity(agentRoute: AgentRoute): AgentRouteEntity {
        return AgentRouteEntity(
            agentRoute.id,
            agentRoute.userId,
            agentRoute.routeId,
            agentRoute.status,
            agentRoute.createdAt,
            agentRoute.updatedAt)
    }

    private fun mapAgentRouteEntityToAgentRoute(agentRouteEntity: AgentRouteEntity): AgentRoute {
        return AgentRoute(
            agentRouteEntity.id,
            agentRouteEntity.userId,
            agentRouteEntity.routeId,
            agentRouteEntity.status,
            agentRouteEntity.createdAt,
            agentRouteEntity.updatedAt
        )
    }

    fun mapListOfAgentRouteToListOfAgentRouteEntity(routes: List<AgentRoute>): List<AgentRouteEntity> {
        return routes.map { route -> mapAgentRouteToAgentRouteEntity(route) }
    }

    fun mapListOfAgentRouteEntityToListOfAgentRoute(routes: List<AgentRouteEntity>): List<AgentRoute> {
        return routes.map { route -> mapAgentRouteEntityToAgentRoute(route) }
    }

    private fun mapUpdateEntityToUpdate(update: UpdateEntity): Update {
        return Update(update.id, update.title, update.body, update.time)
    }

    private fun mapUpdateToUpdateEntity(update: Update): UpdateEntity {
        return UpdateEntity(update.id, update.title, update.body, update.time)
    }

    fun mapListOfUpdateEntityToListOfUpdate(updates: List<UpdateEntity>): List<Update> {
        return updates.map { updateEntity ->
            mapUpdateEntityToUpdate(updateEntity)
        }

    }

    fun mapListOfUpdateToListOfUpdateEntity(updates: List<Update>): List<UpdateEntity> {
        return updates.map { update ->
            mapUpdateToUpdateEntity(update)
        }

    }
}