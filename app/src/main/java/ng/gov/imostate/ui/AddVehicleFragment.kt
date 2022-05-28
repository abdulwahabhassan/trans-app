package ng.gov.imostate.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.Mapper
import ng.gov.imostate.R
import ng.gov.imostate.adapter.RoutesAdapter
import ng.gov.imostate.databinding.FragmentAddVehicleBinding
import ng.gov.imostate.databinding.FragmentFindVehicleDialogBinding
import ng.gov.imostate.databinding.LayoutSelectRoutesBinding
import ng.gov.imostate.model.domain.Route
import ng.gov.imostate.model.domain.TransactionData
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.DateInputMask
import ng.gov.imostate.viewmodel.AddVehicleFragmentViewModel
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    private val mainBinding get() = _binding!!
    private var lgaIndex: Int  = 0
    private var validationErrorMessage: String = ""
    private var selectedRoutes = mutableListOf<Route>()
    private var vehicleCategories = listOf<String>()
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    lateinit var viewModel: AddVehicleFragmentViewModel
    private lateinit var routesAdapter: RoutesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return mainBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(AddVehicleFragmentViewModel::class.java)

        showMinimal()

        setUpBankNameSpinner()

        setUpStateOfRegistrationSpinner()

        setUpStateOfOriginSpinner()

        setUpLocalGovernmentAreaSpinner()

        setUpVehicleCategorySpinner()

        with(mainBinding) {
            DateInputMask(roadWorthinessExpiryDateET).listen()
            DateInputMask(vehicleLicenseExpiryDateET).listen()
            DateInputMask(vehicleInsuranceExpiryDateET).listen()
            DateInputMask(dobET).listen()

            backArrowIV.setOnClickListener {
                findNavController().popBackStack()
            }

            minimalCB.setOnClickListener { cb ->
                if (fullCB.isChecked) {
                    fullCB.isChecked = false
                }
                (cb as MaterialCheckBox).isChecked = true
                showMinimal()
            }

            fullCB.setOnClickListener { cb ->
                if (minimalCB.isChecked) {
                    minimalCB.isChecked = false
                }
                (cb as MaterialCheckBox).isChecked = true
                showFull()
            }

            autoFillBTN.setOnClickListener {
                showFindVehicleDialog()
            }

            selectedVehicleRoutesTV.setOnClickListener {
                showSelectRoutesDialog()
            }

            addVehicleBTN.setOnClickListener {
                if(validateFields()) {
                    val selectedBank = bankNameSpinner.selectedItem
                    val selectedStateOfReg = stateOfRegistrationSpinner.selectedItem
                    val selectedStateOfOrigin = stateOfOriginSpinner.selectedItem
                    val selectedVehicleCategory = vehicleCategorySpinner.selectedItem
                    val selectedLocalGovtArea = localGovernmentSpinner.selectedItem

                    Timber.d("$selectedBank $selectedLocalGovtArea $selectedStateOfOrigin $selectedStateOfReg $selectedVehicleCategory $selectedRoutes")

                    val fleetNumber = fleetNumberET.text.toString()
                    val plateNumber = plateNumberET.text.toString()
                    val chassisNumber = chasisNumberET.text.toString()
                    val engineNumber = engineNumberET.text.toString()
                    val vehicleBrand = vehicleBrandET.text.toString()
                    val vehicleModel = vehicleModelET.text.toString()
                    val passengerCapacity = passengerCapacityET.text.toString()
                    val driverFirstName = driverFirstNameET.text.toString()
                    val driverMiddleName = driverMiddleNameET.text.toString()
                    val driverLastName = driverLastNameET.text.toString()
                    val dob = dobET.text.toString()
                    val imssin = imssinNumberET.text.toString()
                    val idNumber =  idNumberET.text.toString()
                    val phoneNumber = phoneNumberET.text.toString()
                    val email = emailET.text.toString()
                    val residentialAddress = residentialAddressET.text.toString()
                    val bankAcctNumber = acctNumberET.text.toString()
                    val bvn = bvnET.text.toString()
                    val roadWorthinessExpiryDate = roadWorthinessExpiryDateET.text.toString()
                    val vehicleLicenseExpiryDate = vehicleLicenseExpiryDateET.text.toString()
                    val vehicleInsuranceExpiryDate = vehicleInsuranceExpiryDateET.text.toString()
                    val selectedDriverGender = mainBinding.root.findViewById<AppCompatRadioButton>(driverGenderRG.checkedRadioButtonId)?.text ?: ""
                    val selectedMaritalStatus = mainBinding.root.findViewById<AppCompatRadioButton>(maritalStatusRG.checkedRadioButtonId)?.text ?: ""
                    val selectedBloodGroup = mainBinding.root.findViewById<AppCompatRadioButton>(bloodGroupRG.checkedRadioButtonId)?.text ?: ""
                    val selectedMeansOfId = mainBinding.root.findViewById<AppCompatRadioButton>(meansOfIdRG.checkedRadioButtonId)?.text ?: ""

                    val onboardVehicleRequest = OnboardVehicleRequest(
                        vehiclePlates = plateNumber,
                        fleetNumber = fleetNumber,
                        brand = vehicleBrand,
                        type = selectedVehicleCategory.toString(),
                        firstName = driverFirstName,
                        middleName = driverMiddleName.ifEmpty { "-" },
                        lastName = driverLastName,
                        dOB = dob,
                        address = residentialAddress,
                        imssin = imssin,
                        email = email,
                        phone = phoneNumber,
                        routes = selectedRoutes.map { Route(routeID = it.id) },
                        stateOfRegistration = selectedStateOfReg.toString(),
                        gender = selectedDriverGender.toString(),
                        maritalStatus = selectedMaritalStatus.toString(),
                        bloodGroup = selectedBloodGroup.toString(),
                        passengerCapacity = passengerCapacity,
                        chasisNumber = chassisNumber,
                        engineNumber = engineNumber,
                        vehicleModel = vehicleModel,
                        meansOfID = selectedMeansOfId.toString(),
                        idNumber = idNumber,
                        stateOfOrigin = selectedStateOfOrigin.toString(),
                        localGovt = selectedLocalGovtArea.toString(),
                        bankName = selectedBank.toString(),
                        accountNumber = bankAcctNumber,
                        bvn = bvn,
                        roadWorthinessExpDate = roadWorthinessExpiryDate,
                        vehicleLicenceExpDate = vehicleLicenseExpiryDate,
                        vehicleInsuranceExpDate = vehicleInsuranceExpiryDate
                    )

                    Timber.d("$onboardVehicleRequest")

                    viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                        AppUtils.showProgressIndicator(true, mainBinding.progressIndicator)
                        AppUtils.showView(false, mainBinding.addVehicleBTN)
                        val result = viewModel.getInitialUserPreferences().token?.let { token ->
                            viewModel.onboardVehicle(token, onboardVehicleRequest)
                        }!!
                        when (result) {
                            is ViewModelResult.Success -> {

                                Timber.d("onboarded vehicle: ${result.data}")

                                AppUtils.showToast(requireActivity(), "Successful", MotionToastStyle.SUCCESS)
                                //insert/update newly created transaction to app's database to be synced later to cloud database/server
                                viewModel.insertTransactionToDatabase(
                                    TransactionData(result.data?.onBoardVehicle?.id.toString(),
                                        AppUtils.getCurrentDate(),
                                        0.00
                                    )
                                )

                                val onBoardedVehicle = result.data?.onBoardVehicle
                                val action = AddVehicleFragmentDirections
                                    .actionAddVehicleFragmentToSuccessFragment(
                                        onBoardedVehicle?.id.toString(),
                                        onBoardedVehicle?.vehiclePlates,
                                        onBoardedVehicle?.driverName,
//                                    AppUtils.getCurrentDate(),
                                        "2022-04-01",
                                        onBoardedVehicle?.type
                                    )
                                findNavController().navigate(action)
                            }
                            is ViewModelResult.Error -> {

                                Timber.d(result.errorMessage)

                                AppUtils.showToast(requireActivity(), result.errorMessage, MotionToastStyle.ERROR)
                                AppUtils.showProgressIndicator(false, mainBinding.progressIndicator)
                                AppUtils.showView(true, mainBinding.addVehicleBTN)
                            }
                        }
                    }
                } else {
                    AppUtils.showToast(requireActivity(), "Incomplete field(s)! $validationErrorMessage", MotionToastStyle.ERROR)
                }
            }
        }
    }

    private fun showSelectRoutesDialog() {
        val binding = LayoutSelectRoutesBinding.inflate(
            LayoutInflater.from(requireContext()),
            this.mainBinding.root,
            false
        )

        val selectRoutesSheet = BottomSheetDialog(requireContext())
        selectRoutesSheet.setContentView(binding.root)
        selectRoutesSheet.dismissWithAnimation = true
        selectRoutesSheet.setCancelable(false)

        selectRoutesSheet.show()

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                AppUtils.showToast(requireActivity(), newText, MotionToastStyle.INFO)
                return true
            }

        })

        binding.doneBTN.setOnClickListener {
            selectRoutesSheet.dismiss()
            mainBinding.selectedVehicleRoutesTV.text = selectedRoutes.map { "${it.from?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) 
                else it.toString() 
            }} to ${it.to?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) 
                else it.toString() }}"}.toString()
                .removePrefix("[").removeSuffix("]")
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            Timber.d("Ok")
            AppUtils.showView(false, binding.routesRV)
            routesAdapter = RoutesAdapter { position, itemAtPosition ->
                itemAtPosition.selected = !itemAtPosition.selected
                if (itemAtPosition.selected) {
                    if (!selectedRoutes.contains(itemAtPosition)) {
                        selectedRoutes.add(itemAtPosition)
                    }
                } else {
                    if (selectedRoutes.contains(itemAtPosition)) {
                        selectedRoutes.remove(itemAtPosition)
                    }
                }
                routesAdapter.notifyItemChanged(position)
            }
            binding.routesRV.adapter = routesAdapter
            val routes = Mapper.mapListOfRouteEntityToListOfRoute(viewModel.getAllRoutesInDatabase())
            Timber.d("$routes")
            if(routes.isNotEmpty()) {
                //show the routes with the latest state of each items based on whether they have
                    //already been selected or not
                val rts = routes.map { route -> selectedRoutes.find { route.id == it.id } ?: route }
                routesAdapter.submitList(rts)
                AppUtils.showView(true, binding.routesRV)
            } else {
                AppUtils.showToast(requireActivity(), "No routes available", MotionToastStyle.INFO)
            }
        }
    }


    //show bottomSheetDialog to find vehicle
    private fun showFindVehicleDialog() {
        val binding = FragmentFindVehicleDialogBinding.inflate(
            LayoutInflater.from(requireContext()),
            mainBinding.root,
            false
        )
        val findVehicleBottomSheetDialog = BottomSheetDialog(requireContext())
        findVehicleBottomSheetDialog.setContentView(binding.root)

        findVehicleBottomSheetDialog.show()

        binding.backArrowIV.setOnClickListener {
            findVehicleBottomSheetDialog.dismiss()
        }

        binding.continueBTN.setOnClickListener {
            binding.vehicleDetailsTIP.error = ""
            if (binding.platesNumberET.text.toString().isEmpty()) {
                binding.vehicleDetailsTIP.error = "Please input vehicle plate number"
            } else {
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    val vehicle = viewModel.findVehicleDriverRecordFromPreviousEnumerationInDatabase(
                        binding.platesNumberET.text.toString()
                    )
                    if (vehicle != null) {
                        Timber.d("$vehicle")
                        with(this@AddVehicleFragment.mainBinding) {

                            //text fields
                            fleetNumberET.setText(vehicle.fleetNumber)
                            plateNumberET.setText(vehicle.vehiclePlates)
                            vehicleBrandET.setText(vehicle.brand)
                            vehicleModelET.setText(vehicle.vehicleModel)
                            passengerCapacityET.setText(vehicle.passengerCapacity)
                            driverFirstNameET.setText(vehicle.driver?.firstName)
                            driverMiddleNameET.setText(vehicle.driver?.middleName)
                            driverLastNameET.setText(vehicle.driver?.lastName)
                            dobET.setText(vehicle.driver?.dOB)
                            idNumberET.setText(vehicle.driver?.idNumber)
                            imssinNumberET.setText(vehicle.driver?.imssin)
                            phoneNumberET.setText(vehicle.driver?.phone)
                            emailET.setText(vehicle.driver?.email)
                            residentialAddressET.setText(vehicle.driver?.address)
                            acctNumberET.setText(vehicle.driver?.accountNumber)
                            bvnET.setText(vehicle.driver?.bvn)
                            chasisNumberET.setText(vehicle.chasisNumber)
                            engineNumberET.setText(vehicle.engineNumber)
                            roadWorthinessExpiryDateET.setText(vehicle.roadWorthinessExpDate)
                            vehicleLicenseExpiryDateET.setText(vehicle.vehicleLicenceExpDate)
                            vehicleInsuranceExpiryDateET.setText(vehicle.vehicleInsuranceExpDate)

                            //spinners
                            stateOfRegistrationSpinner.setSelection(
                                if (resources.getStringArray(R.array.nigerian_states)
                                        .indexOf(vehicle.stateOfRegistration?.let { it1 ->
                                            AppUtils.capitalize(it1) } ?:
                                        resources.getStringArray(R.array.nigerian_states).first()) != -1)
                                    resources.getStringArray(R.array.nigerian_states)
                                        .indexOf(vehicle.stateOfRegistration?.let { it1 ->
                                            AppUtils.capitalize(it1) } ?:
                                        resources.getStringArray(R.array.nigerian_states).first())
                                else 0,
                                true
                            )
                            vehicleCategorySpinner.setSelection(
                               if (vehicleCategories.indexOf(vehicle.type ?:
                                   vehicleCategories.first()) != -1)
                                   vehicleCategories.indexOf(vehicle.type ?:
                                   vehicleCategories.first())
                               else 0,
                                true
                            )
                            stateOfOriginSpinner.setSelection(
                                if (resources.getStringArray(R.array.nigerian_states)
                                        .indexOf(vehicle.driver?.stateOfOrigin?.let { it1 ->
                                                AppUtils.capitalize(it1) } ?:
                                        resources.getStringArray(R.array.nigerian_states).first()) != -1)
                                    resources.getStringArray(R.array.nigerian_states)
                                        .indexOf(vehicle.driver?.stateOfOrigin?.let { it1 ->
                                            AppUtils.capitalize(it1) } ?:
                                        resources.getStringArray(R.array.nigerian_states).first())
                                else 0,
                                true
                            )

                            val stringArrayResId = AppUtils.mapOfLgaStringArray()[vehicle.driver?.stateOfOrigin]
                            lgaIndex = if (stringArrayResId != null) {
                                resources.getStringArray(stringArrayResId).indexOf(vehicle.driver?.localGovt)
                            } else {
                                0
                            }
                            Timber.d("index: $lgaIndex")
                            if(vehicle.driver?.stateOfOrigin != null) {
                                setUpLocalGovernmentAreaSpinner(vehicle.driver.stateOfOrigin, lgaIndex)
                            }

                            bankNameSpinner.setSelection(
                                if (resources.getStringArray(R.array.banks_names)
                                        .indexOf(vehicle.driver?.bankName ?: "Access Bank Plc") != -1)
                                            resources.getStringArray(R.array.banks_names)
                                    .indexOf(vehicle.driver?.bankName ?: "Access Bank Plc")
                                else 0,
                                true
                            )
                            selectedRoutes = vehicle.routes?.let { it1 ->
                                Mapper.mapListOfRouteEntityToListOfRoute(
                                    it1
                                ).toMutableList()
                            } ?: emptyList<Route>().toMutableList()

                            selectedVehicleRoutesTV.text = selectedRoutes.map { routeEntity ->
                                routeEntity.from + "to" + routeEntity.to
                            }.toString().removePrefix("[").removeSuffix("]")

                            //radios
                            driverGenderRG.check(
                                if (vehicle.driver?.gender.equals(
                                        "female",
                                        true
                                    )
                                ) R.id.femaleCB else R.id.maleCB
                            )
                            meansOfIdRG.check(
                                when {
                                    vehicle.driver?.meansOfID.equals(
                                        "Permanent Voters Card",
                                        true
                                    ) -> R.id.votersCardRB
                                    vehicle.driver?.meansOfID.equals(
                                        "National ID Card",
                                        true
                                    ) -> R.id.nationalIdCardRB
                                    else -> R.id.ninSlipRB
                                }
                            )

                            maritalStatusRG.check(
                                when {
                                    vehicle.driver?.maritalStatus.equals(
                                        "Divorce",
                                        true
                                    ) -> R.id.divorcedRB
                                    vehicle.driver?.maritalStatus.equals(
                                        "Married",
                                        true
                                    ) -> R.id.marriedRB
                                    else -> R.id.singleRB
                                }
                            )
                            bloodGroupRG.check(
                                when {
                                    vehicle.driver?.bloodGroup.equals(
                                        "A+", true
                                    ) -> R.id.aPositiveRB
                                    vehicle.driver?.bloodGroup.equals(
                                        "B+", true
                                    ) -> R.id.bPositiveRB
                                    vehicle.driver?.bloodGroup.equals(
                                        "AB+", true
                                    ) -> R.id.abPositiveRB
                                    vehicle.driver?.bloodGroup.equals(
                                        "A-", true
                                    ) -> R.id.aNegativeRB
                                    vehicle.driver?.bloodGroup.equals(
                                        "B-", true
                                    ) -> R.id.bNegativeRB
                                    vehicle.driver?.bloodGroup.equals(
                                        "AB-", true
                                    ) -> R.id.abNegativeRB
                                    vehicle.driver?.bloodGroup.equals(
                                        "O-", true
                                    ) -> R.id.oNegativeRB
                                    else -> R.id.oPositiveRB
                                }
                            )
                        }
                    } else {
                        AppUtils.showToast(requireActivity(), "Vehicle not found", MotionToastStyle.ERROR)
                    }
                    findVehicleBottomSheetDialog.dismiss()
                }
            }
        }

    }

    private fun validateFields(): Boolean {
        var successful = true

        with(mainBinding) {
            if (fleetNumberTIL.isVisible && fleetNumberET.text.isNullOrEmpty()){
                fleetNumberTIL.error = "Please fill fleet number"
                successful = false
            } else {
                fleetNumberTIL.error = ""
            }
            if (plateNumberTIL.isVisible && plateNumberET.text.isNullOrEmpty()){
                plateNumberTIL.error = "Please fill plate number"
                successful = false
            } else {
                plateNumberTIL.error = ""
            }
            if (chasisNumberTIL.isVisible && chasisNumberET.text.isNullOrEmpty()){
                chasisNumberTIL.error = "Please fill chassis number"
                successful = false
            } else {
                chasisNumberTIL.error = ""
            }
            if (engineNumberTIL.isVisible && engineNumberET.text.isNullOrEmpty()){
                engineNumberTIL.error = "Please fill engine number"
                successful = false
            } else {
                engineNumberTIL.error = ""
            }
            if (vehicleBrandTIL.isVisible && vehicleBrandET.text.isNullOrEmpty()){
                vehicleBrandTIL.error = "Please fill vehicle brand"
                successful = false
            } else {
                vehicleBrandTIL.error = ""
            }
            if (vehicleModelTIL.isVisible && vehicleModelET.text.isNullOrEmpty()){
                vehicleModelTIL.error = "Please fill vehicle model"
                successful = false
            } else {
                vehicleModelTIL.error = ""
            }
            if (passengerCapacityTIL.isVisible && passengerCapacityET.text.isNullOrEmpty()){
                passengerCapacityTIL.error = "Please fill passenger capacity"
                successful = false
            } else {
                passengerCapacityTIL.error = ""
            }
            if (driverFirstNameTIL.isVisible && driverFirstNameET.text.isNullOrEmpty()){
                driverFirstNameTIL.error = "Please fill driver's first name"
                successful = false
            } else {
                driverFirstNameTIL.error = ""
            }
            if (driverLastNameTIL.isVisible && driverLastNameET.text.isNullOrEmpty()){
                driverLastNameTIL.error = "Please fill driver's last name"
                successful = false
            } else {
                driverLastNameTIL.error = ""
            }
            if (imssinNumberTIL.isVisible && imssinNumberET.text.isNullOrEmpty()){
                imssinNumberTIL.error = "Please fill IMSSIN"
                successful = false
            } else {
                imssinNumberTIL.error = ""
            }
            if (dobTIL.isVisible && dobET.text.isNullOrEmpty()){
                dobTIL.error = "Please fill date of birth"
                successful = false
            } else if (dobET.text?.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) == false) {
                dobTIL.error = "Please fill correct date format"
                successful = false
            } else {
                dobTIL.error = ""
            }
            if(idNumberTIL.isVisible && idNumberET.text.isNullOrEmpty()){
                idNumberTIL.error = "Please fill id number"
                successful = false
            } else {
                idNumberTIL.error = ""
            }
            if (phoneNumberTIL.isVisible && phoneNumberET.text.isNullOrEmpty()){
                phoneNumberTIL.error = "Please fill phone number"
                successful = false
            } else {
                phoneNumberTIL.error = ""
            }
            if (emailTIL.isVisible && emailET.text.isNullOrEmpty()){
                emailTIL.error = "Please fill email address"
                successful = false
            } else {
                emailTIL.error = ""
            }
            if (residentialAddressTIL.isVisible && residentialAddressET.text.isNullOrEmpty()){
                residentialAddressTIL.error = "Please fill residential address"
                successful = false
            } else {
                residentialAddressTIL.error = ""
            }
            if (acctNumberTIL.isVisible && acctNumberET.text.isNullOrEmpty()){
                acctNumberTIL.error = "Please fill account number"
                successful = false
            } else {
                acctNumberTIL.error = ""
            }
            if (bvnTIL.isVisible && bvnET.text.isNullOrEmpty()){
                bvnTIL.error = "Please fill BVN"
                successful = false
            } else {
                bvnTIL.error = ""
            }
            if (roadWorthinessExpiryDateTIL.isVisible && roadWorthinessExpiryDateET.text.isNullOrEmpty()){
                roadWorthinessExpiryDateTIL.error = "Please fill road worthiness expiry date"
                successful = false
            } else if (roadWorthinessExpiryDateTIL.isVisible && roadWorthinessExpiryDateET.text?.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) == false) {
                roadWorthinessExpiryDateTIL.error = "Please fill correct date format"
                successful = false
            } else {
                roadWorthinessExpiryDateTIL.error = ""
            }
            if (vehicleLicenseExpiryDateTIL.isVisible && vehicleLicenseExpiryDateET.text.isNullOrEmpty()){
                vehicleLicenseExpiryDateTIL.error = "Please fill vehicle license expiry date"
                successful = false
            } else if (vehicleLicenseExpiryDateTIL.isVisible && vehicleLicenseExpiryDateET.text?.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) == false) {
                vehicleLicenseExpiryDateTIL.error = "Please fill correct date format"
                successful = false
            } else {
                vehicleLicenseExpiryDateTIL.error = ""
            }
            if (vehicleInsuranceExpiryDateTIL.isVisible && vehicleInsuranceExpiryDateET.text.isNullOrEmpty()){
                vehicleInsuranceExpiryDateTIL.error = "Please fill vehicle insurance  expiry date"
                successful = false
            } else if (vehicleInsuranceExpiryDateTIL.isVisible && vehicleInsuranceExpiryDateET.text?.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) == false) {
                vehicleInsuranceExpiryDateTIL.error = "Please fill correct date format"
                successful = false
            } else {
                vehicleInsuranceExpiryDateTIL.error = ""
            }

            val selectedBank = bankNameSpinner.selectedItem
            val selectedStateOfReg = stateOfRegistrationSpinner.selectedItem
            val selectedStateOfOrigin = stateOfOriginSpinner.selectedItem
            val selectedVehicleCategory = vehicleCategorySpinner.selectedItem
            val selectedLocalGovtArea = localGovernmentSpinner.selectedItem

            Timber.d("$selectedBank $selectedLocalGovtArea $selectedStateOfOrigin $selectedStateOfReg $selectedVehicleCategory $selectedRoutes")

            val selectedDriverGender = mainBinding.root.findViewById<AppCompatRadioButton>(driverGenderRG.checkedRadioButtonId)?.text ?: ""
            val selectedMaritalStatus = mainBinding.root.findViewById<AppCompatRadioButton>(maritalStatusRG.checkedRadioButtonId)?.text ?: ""
            val selectedBloodGroup = mainBinding.root.findViewById<AppCompatRadioButton>(bloodGroupRG.checkedRadioButtonId)?.text ?: ""
            val selectedMeansOfId = mainBinding.root.findViewById<AppCompatRadioButton>(meansOfIdRG.checkedRadioButtonId)?.text ?: ""

            Timber.d("$selectedDriverGender $selectedMaritalStatus $selectedBloodGroup $selectedMeansOfId")

            if (driverGenderRG.isVisible && selectedDriverGender.isEmpty()) {
                validationErrorMessage = "Select driver's gender"
                successful = false
            } else {
                validationErrorMessage = ""
            }
            if (maritalStatusRG.isVisible && selectedMaritalStatus.isEmpty()) {
                validationErrorMessage = "Select marital status"
                successful = false
            } else {
                validationErrorMessage = ""
            }
            if (bloodGroupRG.isVisible && selectedBloodGroup.isEmpty()) {
                validationErrorMessage = "Select blood group"
                successful = false
            } else {
                validationErrorMessage = ""
            }
            if (meansOfIdRG.isVisible && selectedMeansOfId.isEmpty()) {
                validationErrorMessage = "Select means of ID"
                successful = false
            } else {
                validationErrorMessage = ""
            }
            if (selectedRoutes.isEmpty()) {
                Timber.d("validate routes: $selectedRoutes")
                validationErrorMessage = "Select route(s)"
                successful = false
            } else {
                validationErrorMessage = ""
            }
        }
        return successful
    }


    private fun setUpBankNameSpinner(){

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.banks_names, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mainBinding.bankNameSpinner.adapter = spinnerAdapter
        mainBinding.bankNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpStateOfOriginSpinner(){

        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.nigerian_states, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mainBinding.stateOfOriginSpinner.adapter = spinnerAdapter
        mainBinding.stateOfOriginSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {
                val state = resources.getStringArray(R.array.nigerian_states)[position]
                setUpLocalGovernmentAreaSpinner(state, lgaIndex)
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpLocalGovernmentAreaSpinner(state: String = "Abia", position: Int = 0){
        val spinnerAdapter = AppUtils.mapOfLgaStringArray()[state]?.let { resId ->
            Timber.d("resId ${resId}")
            ArrayAdapter.createFromResource(
                requireContext(), resId, android.R.layout.simple_spinner_item
            )
        }
        spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mainBinding.localGovernmentSpinner.adapter = spinnerAdapter
        mainBinding.localGovernmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {
                lgaIndex = 0 //reset index
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        mainBinding.localGovernmentSpinner.setSelection(position)
    }

    private fun setUpStateOfRegistrationSpinner(){
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.nigerian_states, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mainBinding.stateOfRegistrationSpinner.adapter = spinnerAdapter
        mainBinding.stateOfRegistrationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpVehicleCategorySpinner(){
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            vehicleCategories = viewModel.getCategories().distinct()
            Timber.d("$vehicleCategories")
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, vehicleCategories)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mainBinding.vehicleCategorySpinner.adapter = spinnerAdapter
            mainBinding.vehicleCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        }
    }

    private fun showFull() {
        with(mainBinding) {
//until verified to be non-compulsory
//            imssinNumberTV.visibility = VISIBLE
//            imssinNumberTIL.visibility = VISIBLE

            roadWorthinessExpiryDateTV.visibility = VISIBLE
            roadWorthinessExpiryDateTIL.visibility = VISIBLE

            vehicleInsuranceExpiryDateTV.visibility = VISIBLE
            vehicleInsuranceExpiryDateTIL.visibility = VISIBLE

            vehicleLicenseExpiryDateTV.visibility = VISIBLE
            vehicleLicenseExpiryDateTIL.visibility = VISIBLE

            bloodGroupTV.visibility = VISIBLE
            bloodGroupRG.visibility = VISIBLE

            maritalStatusTV.visibility = VISIBLE
            maritalStatusRG.visibility = VISIBLE

            engineNumberTV.visibility = VISIBLE
            engineNumberTIL.visibility = VISIBLE

            chasisNumberTV.visibility = VISIBLE
            chasisNumberTIL.visibility = VISIBLE

            stateOfRegistrationTV.visibility = VISIBLE
            stateOfRegistrationSpinner.visibility = VISIBLE
        }
    }

    private fun showMinimal() {
        with(mainBinding) {

//until verified to be non-compulsory
//            imssinNumberTV.visibility = GONE
//            imssinNumberTIL.visibility = GONE

            roadWorthinessExpiryDateTV.visibility = GONE
            roadWorthinessExpiryDateTIL.visibility = GONE

            vehicleInsuranceExpiryDateTV.visibility = GONE
            vehicleInsuranceExpiryDateTIL.visibility = GONE

            vehicleLicenseExpiryDateTV.visibility = GONE
            vehicleLicenseExpiryDateTIL.visibility = GONE

            bloodGroupTV.visibility = GONE
            bloodGroupRG.visibility = GONE

            maritalStatusTV.visibility = GONE
            maritalStatusRG.visibility = GONE

            engineNumberTV.visibility = GONE
            engineNumberTIL.visibility = GONE

            chasisNumberTV.visibility = GONE
            chasisNumberTIL.visibility = GONE

            stateOfRegistrationTV.visibility = GONE
            stateOfRegistrationSpinner.visibility = GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}