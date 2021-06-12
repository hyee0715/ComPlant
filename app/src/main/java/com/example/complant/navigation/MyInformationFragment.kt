package com.example.complant.navigation

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.complant.MainActivity
import com.example.complant.R
import com.example.complant.navigation.model.MainPageDTO
import com.example.complant.navigation.model.UserInfoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_my_information.*
import kotlinx.android.synthetic.main.fragment_my_information.view.*
import kotlinx.android.synthetic.main.fragment_my_page.view.*
import java.util.*

class MyInformationFragment : Fragment() {
    var mainActivity: MainActivity? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
   // var auth : FirebaseAuth? = null
    var currentUserId : String? = null
//    var currentProfileName : String? = null
//    var currentPlantName : String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity?
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_my_information, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserId = FirebaseAuth.getInstance().currentUser?.email

        var userInfoDTOs = UserInfoDTO.UserInfo()

        // 프래그먼트에 들어오면 현재 정보(정보 변경 전)를 보여준다.
        view.my_info_current_id.setText(currentUserId)

        // DB에서 profile name과 plant name을 가져와서 현재 정보를 보여준다.
        firestore?.collection("userInfo")
            ?.document(uid!!)
            ?.collection("info")
            ?.document(uid!!)
            ?.addSnapshotListener { snapshot, e ->
            if(snapshot == null) return@addSnapshotListener
            var userInfoDTO = snapshot.toObject(UserInfoDTO.UserInfo::class.java)
                view.my_info_current_profile_name.setText(userInfoDTO?.profileName)
                view.edit_profile_name_setting.setText(userInfoDTO?.profileName)
                view.my_info_current_plant_name.setText(userInfoDTO?.plantName)
                view.edit_plant_name_setting.setText(userInfoDTO?.plantName)
                view.my_info_current_plant_type.setText(userInfoDTO?.plantType)
                view.edit_plant_type_setting.setText(userInfoDTO?.plantType)

//                if (userInfoDTO?.startMonth!! < 10 && userInfoDTO?.startDay!! < 10) {
//                    view.btn_start_day_setting.setText(userInfoDTO?.startYear.toString() + "-0" + userInfoDTO?.startMonth.toString() + "-0" + userInfoDTO?.startDay.toString())
//                    view.my_info_current_start_day.setText(userInfoDTO?.startYear.toString() + "-0" + userInfoDTO?.startMonth.toString() + "-0" + userInfoDTO?.startDay.toString())
//                }
//                else if (userInfoDTO?.startMonth!! + 1 < 10) {
//                    view.btn_start_day_setting.setText(userInfoDTO?.startYear.toString() + "-0" + userInfoDTO?.startMonth.toString() + "-" + userInfoDTO?.startDay.toString())
//                    view.my_info_current_start_day.setText(userInfoDTO?.startYear.toString() + "-0" + userInfoDTO?.startMonth.toString() + "-" + userInfoDTO?.startDay.toString())
//                }
//                else if (userInfoDTO?.startDay!! < 10) {
//                    view.btn_start_day_setting.setText(userInfoDTO?.startYear.toString() + "-" + userInfoDTO?.startMonth.toString() + "-0" + userInfoDTO?.startDay.toString())
//                    view.my_info_current_start_day.setText(userInfoDTO?.startYear.toString() + "-" + userInfoDTO?.startMonth.toString() + "-0" + userInfoDTO?.startDay.toString())
//                }
//                else {
//                    view.btn_start_day_setting.setText(userInfoDTO?.startYear.toString() + "-" + userInfoDTO?.startMonth.toString() + "-" + userInfoDTO?.startDay.toString())
//                    view.my_info_current_start_day.setText(userInfoDTO?.startYear.toString() + "-" + userInfoDTO?.startMonth.toString() + "-" + userInfoDTO?.startDay.toString())
//                }
            }


        // 프로필 이미지 클릭 시 프로필 이미지 수정
        view?.profile_image_setting?.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK) // 이미지 고르기
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent,
                MyPageFragment.PICK_PROFILE_FROM_ALBUM
            )
        }

        // 올린 이미지 가져오기
        getProfileImage()

        // 완료 버튼을 누르면 정보 DB에 업데이트
        view.btn_my_info_update.setOnClickListener {
            userInfoDTOs?.profileName = edit_profile_name_setting.text.toString()
            userInfoDTOs?.plantName = edit_plant_name_setting.text.toString()
            userInfoDTOs?.plantType = edit_plant_type_setting.text.toString()
            userInfoDTOs?.uid = uid
            userInfoDTOs?.userId = currentUserId

            //if (userInfoDTOs.startYear != null && userInfoDTOs.startMonth != null && userInfoDTOs.startDay != null) {
                firestore?.collection("userInfo")?.document(uid!!)?.collection("info")?.document(uid!!)
                    ?.set(userInfoDTOs)
           // }

            mainActivity?.goBack()
        }

        // 식물 기르기 시작한 날짜 버튼 클릭
//        view.btn_start_day_setting?.setOnClickListener {
//            var calendar = Calendar.getInstance()
//            var year = calendar.get(Calendar.YEAR)
//            var month = calendar.get(Calendar.MONTH)
//            var day = calendar.get(Calendar.DAY_OF_MONTH)
//
//            var listener = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
//                if (i2 + 1 < 10 && i3 < 10) {
//                    view.btn_start_day_setting.setText("${i}-0${i2 + 1}-0${i3}")
//                    view.my_info_current_start_day.setText("${i}-0${i2 + 1}-0${i3}")
//                }
//                else if (i2 + 1 < 10) {
//                    view.btn_start_day_setting.setText("${i}-0${i2 + 1}-${i3}")
//                    view.my_info_current_start_day.setText("${i}-0${i2 + 1}-${i3}")
//                }
//                else if (i3 < 10) {
//                    view.btn_start_day_setting.setText("${i}-${i2 + 1}-0${i3}")
//                    view.my_info_current_start_day.setText("${i}-${i2 + 1}-0${i3}")
//                }
//                else {
//                    view.btn_start_day_setting.setText("${i}-${i2 + 1}-${i3}")
//                    view.my_info_current_start_day.setText("${i}-${i2 + 1}-${i3}")
//                }
//
//                userInfoDTOs?.startYear = i
//                userInfoDTOs?.startMonth = i2 + 1
//                userInfoDTOs?.startDay = i3
//            }
//
//            var picker = DatePickerDialog(view.context, listener, year, month, day)
//            picker.show()
//        }

        return view
    }

    // 올린 이미지를 다운로드 받는 함수
    fun getProfileImage() {
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener {
                documentSnapshot, firebaseFirestoreException ->

            // documentSnapshot이 null일 경우 바로 리턴
            if(documentSnapshot == null) return@addSnapshotListener

            // null이 아닐 경우 이미지 주소 값을 받아와서 Glide로 다운로드 받는다.
            if(documentSnapshot.data != null) {
                var url = documentSnapshot?.data!!["image"]
                Glide.with(activity!!).load(url).apply(RequestOptions().circleCrop()).into(view?.profile_image_setting!!)
            }
        }
    }
}