import { useEffect, useState } from "react";
import { fetchWithAuth } from "../../../util/AuthUtil";
import { BASE_API_URL } from "../../../constants";
import { toast } from "react-toastify";

export default function AddressInputModal({info, setInfo, setClose, onSuccess}){

    const isNew = info.addressId === null || info.addressId === undefined
    const disabledPrimary = info.primary === true
    const [provinces, setProvinces] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [wards, setWards] = useState([]);

    const fetchProvinceList = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/address/provinces`, window.location, true)
            .then(res => res.json())
            .then(res => {
              setProvinces(res)
              setDistricts([])
              setWards([])
            })
    }

    const fetchDistrictList = (provinceId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/address/districts?provinceId=${provinceId}`, window.location, true)
            .then(res => {
              if(res.ok) return res.json()
              return []
            })
            .then(res => {
              setDistricts(res)
              setWards([])
            })
    }

    const fetchWardList = (districtId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/address/wards?districtId=${districtId}`, window.location, true)
            .then(res => {
              if(res.ok) return res.json()
              return []
            })
            .then(res => setWards(res))
    }

    useEffect(() => {
        fetchProvinceList()
    }, [])
    
    useEffect(() => {
        if(info.province){
            const selectedProvince = provinces.find((province) => province.name === info.province);
            const provinceId = selectedProvince ? selectedProvince.id : 0;
            fetchDistrictList(provinceId)
        }
    }, [info.province])

    useEffect(() => {
        if(info.district){
            const selectedDistrict = districts.find((district) => district.name === info.district);
            const provinceId = selectedDistrict ? selectedDistrict.id : 0;
            fetchWardList(provinceId)
        }
    }, [info.district])

    const handleChangeInfo = (e) => {
        const { name, type, value, checked } = e.target;
    
        setInfo((prev) => ({
        ...prev,
        [name]: type === "checkbox" ? checked : value
        }));
    
        // 🛑 Nếu chọn tỉnh, tìm ID từ danh sách `cities`
        if (name === "province") {
        setInfo((prev) => ({
            ...prev,
            province: value, // Vẫn lưu tên
            district: "",
            ward: ""
        }));
        }
        if (name === "district") {
        setInfo((prev) => ({
            ...prev,
            district: value,
            ward: ""
        }));
        }
    };

    const handleSubmit = () => {
        const { receiverName, phoneNumber, detail, ward, district, province } = info;
        if (!receiverName || !phoneNumber || !detail || !ward || !district || !province) {
            alert("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if(isNew) {
            fetchWithAuth(`${BASE_API_URL}/v1/user/address/add`, window.location, true, {
                method: "POST",
                body: JSON.stringify(info),
                headers: {
                'content-type': 'application/json'
                }
            })
                .then(res => {
                  if(res.message){
                    toast.error(res.message)
                  }
                  else{
                      toast.success(`Thêm địa chỉ thành công`)
                      onSuccess()
                  }
                })
        }
        else{
            fetchWithAuth(`${BASE_API_URL}/v1/user/address/update`, window.location, true, {
                method: "POST",
                body: JSON.stringify(info),
                headers: {
                    'content-type': 'application/json'
                }
            })
                .then(res => {
                    if(res.message){
                        toast.error(res.message)
                    }
                    else{
                        toast.success(`Cập nhật địa chỉ thành công`)
                        onSuccess()
                    }
                })
        }
    }

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-gray-100/85">
          <div className="bg-white p-6 rounded-lg shadow-lg w-150">
            <h3 className="text-lg font-semibold mb-4">{isNew ? 'Địa chỉ mới' : 'Cập nhật địa chỉ'}</h3>

            <div className="space-y-3">
              <div className="flex gap-3">
                <input
                  type="text"
                  name="receiverName"
                  placeholder="Họ tên người nhận"
                  value={info.receiverName}
                  onChange={handleChangeInfo}
                  className="w-1/2 p-2 border rounded"
                />
                <input
                  type="text"
                  name="phoneNumber"
                  placeholder="Số điện thoại"
                  value={info.phoneNumber}
                  onChange={handleChangeInfo}
                  className="w-1/2 p-2 border rounded"
                />
              </div>

              <div className="flex gap-3">
                <select
                  name="province"
                  value={info.province}
                  onChange={handleChangeInfo}
                  className="w-full p-2 border rounded"
                >
                  <option value="">Chọn Tỉnh/Thành phố</option>
                  {provinces.map((province) => (
                    <option key={province.id} value={province.name}>
                      {province.name}
                    </option>
                  ))}
                </select>
                <select
                  name="district"
                  value={info.district}
                  onChange={handleChangeInfo}
                  className="w-full p-2 border rounded"
                  disabled={!info.province}
                >
                  <option value="">Chọn Quận/Huyện</option>
                  {districts.map((district) => (
                    <option key={district.id} value={district.name}>
                      {district.name}
                    </option>
                  ))}
                </select>
                <select
                  name="ward"
                  value={info.ward}
                  onChange={handleChangeInfo}
                  className="w-full p-2 border rounded"
                  disabled={!info.district}
                >
                  <option value="">Chọn Xã/Phường</option>
                  {wards.map((ward) => (
                    <option key={ward.id} value={ward.name}>
                      {ward.name}
                    </option>
                  ))}
                </select>
              </div>

              <input
                type="text"
                name="detail"
                placeholder="Chi tiết"
                value={info.detail}
                onChange={handleChangeInfo}
                className="w-full p-2 border rounded"
              />

              <label>
                <input
                  disabled={disabledPrimary}
                  type="checkbox"
                  name="primary"
                  checked={info.primary}
                  onChange={handleChangeInfo}
                />
                Đặt làm mặc định
              </label>

              <div className="flex justify-between items-center mt-4">
                <button className="text-gray-500 hover:underline" onClick={setClose}>Hủy</button>
                <button 
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600" 
                    onClick={() => handleSubmit()} 
                >
                  {isNew ? 'Thêm' : 'Cập nhật'}
                </button>
              </div>
            </div>
          </div>
          
        </div>
    )
}