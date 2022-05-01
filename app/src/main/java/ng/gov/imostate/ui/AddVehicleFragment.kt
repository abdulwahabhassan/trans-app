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
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import ng.gov.imostate.BuildConfig
import ng.gov.imostate.R
import ng.gov.imostate.databinding.FragmentAddVehicleBinding
import ng.gov.imostate.model.domain.Route
import ng.gov.imostate.model.request.OnboardVehicleRequest
import ng.gov.imostate.model.result.ViewModelResult
import ng.gov.imostate.util.AppUtils
import ng.gov.imostate.util.DateInputMask
import ng.gov.imostate.viewmodel.AddVehicleFragmentViewModel
import ng.gov.imostate.viewmodel.AppViewModelsFactory
import timber.log.Timber
import www.sanju.motiontoast.MotionToastStyle
import javax.inject.Inject

@AndroidEntryPoint
class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var appViewModelFactory: AppViewModelsFactory
    //view model
    lateinit var viewModel: AddVehicleFragmentViewModel
    val routes = mutableListOf<String>()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            appViewModelFactory
        ).get(AddVehicleFragmentViewModel::class.java)

        if (BuildConfig.DEBUG) {
            with(binding) {
                fleetNumberET.setText("627JK2787")
                plateNumberET.setText("IMO-1526-HDB")
                vehicleBrandET.setText("Toyota")
                vehicleModelET.setText("Camry")
                passengerCapacityET.setText("4")
                driverFirstNameET.setText("Adams")
                driverMiddleNameET.setText("Jones")
                driverLastNameET.setText("Maple")
                dobET.setText("1987/12/01")
                driverGenderRG.check(R.id.maleCB)
                meansOfIdRG.check(R.id.ninSlipRB)
                idNumberET.setText("NG2728301")
                imssinNumberET.setText("IMS73630")
                phoneNumberET.setText("09022332211")
                emailET.setText("abc@gmail.com")
                residentialAddressET.setText("No.16 new owerri")
                acctNumberET.setText("2302928190")
                bvnET.setText("400019229")
            }
        }

        showMinimal()

        setUpBankNameSpinner()

        setUpLocalGovernmentAreaSpinner()

        setUpStatesSpinner()

        setUpVehicleCategorySpinner()

        setUpVehicleRoutesSpinner()

        with(binding) {

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
            addVehicleBTN.setOnClickListener {
                if(validateFields()) {
                    val selectedBank = resources.getStringArray(R.array.banks_names)[bankNameSpinner.selectedItemPosition]
                    val selectedStateOfReg = resources.getStringArray(R.array.nigerian_states)[stateOfRegistrationSpinner.selectedItemPosition]
                    val selectedStateOfOrigin = resources.getStringArray(R.array.nigerian_states)[stateOfOriginSpinner.selectedItemPosition]
                    val selectedVehicleCategory = resources.getStringArray(R.array.vehicle_category)[vehicleCategorySpinner.selectedItemPosition]
                    val selectedLocalGovtArea = resources.getStringArray(R.array.imo_state_local_government_areas)[localGovernmentSpinner.selectedItemPosition]

                    Timber.d("$selectedBank $selectedLocalGovtArea $selectedStateOfOrigin $selectedStateOfReg $selectedVehicleCategory $routes")

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
                    val selectedDriverGender = binding.root.findViewById<AppCompatRadioButton>(driverGenderRG.checkedRadioButtonId)?.text ?: ""
                    val selectedMaritalStatus = binding.root.findViewById<AppCompatRadioButton>(maritalStatusRG.checkedRadioButtonId)?.text ?: ""
                    val selectedBloodGroup = binding.root.findViewById<AppCompatRadioButton>(bloodGroupRG.checkedRadioButtonId)?.text ?: ""
                    val selectedMeansOfId = binding.root.findViewById<AppCompatRadioButton>(meansOfIdRG.checkedRadioButtonId)?.text ?: ""

                    val onboardVehicleRequest = OnboardVehicleRequest(
                        vehiclePlates = plateNumber,
                        fleetNumber = fleetNumber,
                        brand = vehicleBrand,
                        type = selectedVehicleCategory,
                        firstName = driverFirstName,
                        middleName = driverMiddleName,
                        lastName = driverLastName,
                        dOB = dob,
                        address = residentialAddress,
                        imssin = imssin,
                        email = email,
                        phone = phoneNumber,
                        routes = routes.map { Route(routeID = it.toLong()) },
                        stateOfRegistration = selectedStateOfReg,
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
                        AppUtils.showProgressIndicator(true, binding.progressIndicator)
                        AppUtils.showView(false, binding.addVehicleBTN)
                        val result = viewModel.getInitialUserPreferences().token?.let { token ->
                            viewModel.onboardVehicle(token, onboardVehicleRequest)
                        }!!
                        when (result) {
                            is ViewModelResult.Success -> {
                                Timber.d("${result.data}")
                                AppUtils.showToast(requireActivity(), "Successful", MotionToastStyle.SUCCESS)
                                findNavController().popBackStack()
                            }
                            is ViewModelResult.Error -> {
                                Timber.d(result.errorMessage)
                                AppUtils.showToast(requireActivity(), result.errorMessage, MotionToastStyle.ERROR)
                                AppUtils.showProgressIndicator(false, binding.progressIndicator)
                                AppUtils.showView(true, binding.addVehicleBTN)
                            }
                        }
                    }
                } else {
                    AppUtils.showToast(requireActivity(), "Incomplete fields", MotionToastStyle.ERROR)
                }
            }
        }
    }

    private fun validateFields(): Boolean {
        var successful = true

        with(binding) {
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
            } else if (dobET.text?.matches(Regex("\\d{4}/\\d{2}/\\d{2}")) == false) {
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
            } else if (roadWorthinessExpiryDateTIL.isVisible && roadWorthinessExpiryDateET.text?.matches(Regex("\\d{4}/\\d{2}/\\d{2}")) == false) {
                roadWorthinessExpiryDateTIL.error = "Please fill correct date format"
                successful = false
            } else {
                roadWorthinessExpiryDateTIL.error = ""
            }
            if (vehicleLicenseExpiryDateTIL.isVisible && vehicleLicenseExpiryDateET.text.isNullOrEmpty()){
                vehicleLicenseExpiryDateTIL.error = "Please fill vehicle license expiry date"
                successful = false
            } else if (vehicleLicenseExpiryDateTIL.isVisible && vehicleLicenseExpiryDateET.text?.matches(Regex("\\d{4}/\\d{2}/\\d{2}")) == false) {
                vehicleLicenseExpiryDateTIL.error = "Please fill correct date format"
                successful = false
            } else {
                vehicleLicenseExpiryDateTIL.error = ""
            }
            if (vehicleInsuranceExpiryDateTIL.isVisible && vehicleInsuranceExpiryDateET.text.isNullOrEmpty()){
                vehicleInsuranceExpiryDateTIL.error = "Please fill vehicle insurance  expiry date"
                successful = false
            } else if (vehicleInsuranceExpiryDateTIL.isVisible && vehicleInsuranceExpiryDateET.text?.matches(Regex("\\d{4}/\\d{2}/\\d{2}")) == false) {
                vehicleInsuranceExpiryDateTIL.error = "Please fill correct date format"
                successful = false
            } else {
                vehicleInsuranceExpiryDateTIL.error = ""
            }

            val selectedBank = resources.getStringArray(R.array.banks_names)[bankNameSpinner.selectedItemPosition]
            val selectedStateOfReg = resources.getStringArray(R.array.nigerian_states)[stateOfRegistrationSpinner.selectedItemPosition]
            val selectedStateOfOrigin = resources.getStringArray(R.array.nigerian_states)[stateOfOriginSpinner.selectedItemPosition]
            val selectedVehicleCategory = resources.getStringArray(R.array.vehicle_category)[vehicleCategorySpinner.selectedItemPosition]
            val selectedLocalGovtArea = resources.getStringArray(R.array.imo_state_local_government_areas)[localGovernmentSpinner.selectedItemPosition]

            Timber.d("$selectedBank $selectedLocalGovtArea $selectedStateOfOrigin $selectedStateOfReg $selectedVehicleCategory $routes")

            val selectedDriverGender = binding.root.findViewById<AppCompatRadioButton>(driverGenderRG.checkedRadioButtonId)?.text ?: ""
            val selectedMaritalStatus = binding.root.findViewById<AppCompatRadioButton>(maritalStatusRG.checkedRadioButtonId)?.text ?: ""
            val selectedBloodGroup = binding.root.findViewById<AppCompatRadioButton>(bloodGroupRG.checkedRadioButtonId)?.text ?: ""
            val selectedMeansOfId = binding.root.findViewById<AppCompatRadioButton>(meansOfIdRG.checkedRadioButtonId)?.text ?: ""

            Timber.d("$selectedDriverGender $selectedMaritalStatus $selectedBloodGroup $selectedMeansOfId")

            if (driverGenderRG.isVisible && selectedDriverGender.isEmpty()) {
                successful = false
            }
            if (maritalStatusRG.isVisible && selectedMaritalStatus.isEmpty()) {
                successful = false
            }
            if (bloodGroupRG.isVisible && selectedBloodGroup.isEmpty()) {
                successful = false
            }
            if (meansOfIdRG.isVisible && selectedMeansOfId.isEmpty()) {
                successful = false
            }
            if (routes.isEmpty()) {
                successful = false
            }
        }

        return successful
    }


    private fun setUpBankNameSpinner(){
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.banks_names, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bankNameSpinner.adapter = spinnerAdapter
        binding.bankNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpVehicleRoutesSpinner(){
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.vehicle_routes, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.vehicleRouteSpinner.adapter = spinnerAdapter
        binding.vehicleRouteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = resources.getStringArray(R.array.vehicle_routes)[p2]
                if (routes.contains(selectedItem)) {
                    routes.remove(selectedItem)
                } else {
                    routes.add(selectedItem)
                }
                binding.vehicleRouteTV.text = "Vehicle Routes: " + "${routes}"
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}


        }
    }

    private fun setUpStatesSpinner(){
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.nigerian_states, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.stateOfOriginSpinner.adapter = spinnerAdapter
        binding.stateOfOriginSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        binding.stateOfRegistrationSpinner.adapter = spinnerAdapter
        binding.stateOfRegistrationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpLocalGovernmentAreaSpinner(){
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.imo_state_local_government_areas, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.localGovernmentSpinner.adapter = spinnerAdapter
        binding.localGovernmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setUpVehicleCategorySpinner(){
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.vehicle_category, android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.vehicleCategorySpinner.adapter = spinnerAdapter
        binding.vehicleCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, l: Long) {}
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }




    private fun showFull() {
        with(binding) {
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
        with(binding) {
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