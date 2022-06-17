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
            vehicle.vehicleRoutes?.let { mapListOfVehicleRouteToListOfVehicleRouteEntity(it) }
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
            vehicle.vehicleRoutes?.let { mapListOfVehicleRouteEntityToListOfVehicleRoute(it) },
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
            vehicle.vehicleRoutes?.let { mapListOfVehicleRouteToListOfVehicleRouteEntity(it) }
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

    private fun mapVehicleRouteToVehicleRouteEntity(vehicleRoute: VehicleRoute): VehicleRouteEntity {
        return VehicleRouteEntity(
            vehicleRoute.id,
            vehicleRoute.routeID,
            vehicleRoute.vehicleID,
            vehicleRoute.driverID,
            vehicleRoute.status,
            vehicleRoute.createdAt,
            vehicleRoute.updatedAt,
        )
    }

    private fun mapVehicleRouteEntityToVehicleRoute(vehicleRoute: VehicleRouteEntity): VehicleRoute {
        return VehicleRoute(
            vehicleRoute.id,
            vehicleRoute.routeID,
            vehicleRoute.vehicleID,
            vehicleRoute.driverID,
            vehicleRoute.status,
            vehicleRoute.createdAt,
            vehicleRoute.updatedAt
        )
    }

    fun mapListOfVehicleRouteEntityToListOfVehicleRoute(vehicleRoutes: List<VehicleRouteEntity>): List<VehicleRoute> {
        return vehicleRoutes.map { route ->
            mapVehicleRouteEntityToVehicleRoute(route)
        }
    }

    fun mapListOfVehicleRouteEntityToListOfRoute(vehicleRoutes: List<VehicleRouteEntity>): List<Route> {
        return vehicleRoutes.map { route ->
            mapVehicleRouteEntityToRoute(route)
        }
    }

    private fun mapVehicleRouteEntityToRoute(route: VehicleRouteEntity): Route {
        return Route(
            id = route.routeID,
            status = route.status,
            createdAt = route.createdAt,
            updatedAt = route.updatedAt,
        )
    }

    fun mapListOfVehicleRouteToListOfVehicleRouteEntity(vehicleRoutes: List<VehicleRoute>): List<VehicleRouteEntity> {
        return vehicleRoutes.map { route ->
            mapVehicleRouteToVehicleRouteEntity(route)
        }
    }

    private fun mapRateToRateEntity(rate: Rate): RateEntity {
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

    private fun mapRateEntityToRate(rate: RateEntity): Rate {
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

    fun mapListOfTaxFreeDayToListOfTaxFreeDayEntity(taxFreeDays: List<Holiday>): List<HolidayEntity> {
        return taxFreeDays.map { mapTaxFreeDayToTaxFreeDayEntity(it) }
    }

    private fun mapTaxFreeDayToTaxFreeDayEntity(holiday: Holiday): HolidayEntity {
        return HolidayEntity(
            holiday.id,
            holiday.title,
            holiday.date,
            holiday.routeId,
            holiday.createdAt,
            holiday.updatedAt,
            holiday.status
        )
    }

    fun mapListOfTaxFreeDayEntityToListOfTaxFreeDay(taxFreeDays: List<HolidayEntity>): List<Holiday> {
        return taxFreeDays.map { mapTaxFreeDayEntityToTaxFreeDay(it) }
    }

    private fun mapTaxFreeDayEntityToTaxFreeDay(holiday: HolidayEntity): Holiday {
        return Holiday(
            holiday.id,
            holiday.title,
            holiday.date,
            holiday.routeId,
            holiday.createdAt,
            holiday.updatedAt,
            holiday.status
        )
    }

    fun mapListOfRouteToListOfRouteEntity(routes: List<Route>): List<RouteEntity> {
        return routes.map {
            mapRouteToRouteEntity(it)
        }
    }

    private fun mapRouteToRouteEntity(route: Route): RouteEntity {
        return RouteEntity(
            id = route.id,
            from = route.from,
            to = route.to,
            status = route.status,
            createdAt = route.createdAt,
            updatedAt = route.updatedAt,
        )
    }

    private fun mapRouteEntityToRoute(route: RouteEntity): Route {
        return Route(
            id = route.id,
            from = route.from,
            to = route.to,
            status = route.status,
            createdAt = route.createdAt,
            updatedAt = route.updatedAt,
        )
    }

    fun mapListOfRouteEntityToListOfRoute(routes: List<RouteEntity>): List<Route> {
        return routes.map { mapRouteEntityToRoute(it) }
    }
}