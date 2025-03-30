import { useState, useEffect } from "react";
import { fetchWithAuth } from "../../util/AuthUtil";
import { BASE_API_URL } from "../../constants";

export default function AddressPage(){
    const [addressList, setAddressList] = useState([])
    const [open, setOpen] = useState(false);
    const [newAddress, setNewAddress] = useState({ 
      receiverName: "", 
      phoneNumber: "", 
      detail: "", 
      ward: "", 
      district: "", 
      province: "", 
      primary: false 
    });
    const [provinces, setProvinces] = useState([]);
    const [districts, setDistricts] = useState([]);
    const [wards, setWards] = useState([]);
  
    const handleChangeNewAddress = (e) => {
      const { name, type, value, checked } = e.target;
    
      setNewAddress((prev) => ({
        ...prev,
        [name]: type === "checkbox" ? checked : value
      }));
    
      // 🛑 Nếu chọn tỉnh, tìm ID từ danh sách `cities`
      if (name === "province") {
        setNewAddress((prev) => ({
          ...prev,
          province: value, // Vẫn lưu tên
          district: "",
          ward: ""
        }));
      }
      if (name === "district") {
        setNewAddress((prev) => ({
          ...prev,
          district: value,
          ward: ""
        }));
      }
    };

    const handleAddNewAddress = () => {
      const { receiverName, phoneNumber, detail, ward, district, province } = newAddress;
      if (!receiverName || !phoneNumber || !detail || !ward || !district || !province) {
        alert("Vui lòng điền đầy đủ thông tin!");
        return;
      }

      fetchWithAuth(`${BASE_API_URL}/v1/user/address/add`, window.location, true, {
        method: "POST",
        body: JSON.stringify(newAddress),
        headers: {
          'content-type': 'application/json'
        }
      })
        .then(res => {
          if(res.ok){
            setOpen(false)
            fetchAddressList()
          }
        })
    }

    const fetchAddressList = async () => {
        fetchWithAuth(`${BASE_API_URL}/v1/user/address/get-list`, window.location, true)
            .then(res => res.json())
            .then(data => {
                console.log(data)
                setAddressList(data)
            })
    }

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
        .then(res => res.json())
        .then(res => {
          setDistricts(res)
          setWards([])
        })
    }

    const fetchWardList = (districtId) => {
      fetchWithAuth(`${BASE_API_URL}/v1/address/wards?districtId=${districtId}`, window.location, true)
        .then(res => res.json())
        .then(res => setWards(res))
    }

    useEffect(() => {
        fetchAddressList()
    }, [])

    useEffect(() => {
      if(open){
        fetchProvinceList()
      }
    }, [open])

    useEffect(() => {
      if(newAddress.province){
        const selectedProvince = provinces.find((province) => province.name === newAddress.province);
        const provinceId = selectedProvince ? selectedProvince.id : 0;
        fetchDistrictList(provinceId)
      }
    }, [newAddress.province])

    useEffect(() => {
      if(newAddress.district){
        const selectedDistrict = districts.find((district) => district.name === newAddress.district);
        const provinceId = selectedDistrict ? selectedDistrict.id : 0;
        fetchWardList(provinceId)
      }
    }, [newAddress.district])


  return (
    <div className="p-4 w-300 mx-auto min-h-screen bg-white shadow-md rounded-md">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-semibold">Địa chỉ</h2>
        <button className="cursor-pointer bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600" onClick={() => setOpen(true)}>+ Thêm địa chỉ</button>
      </div>

      {addressList.map((addr) => (
        <div key={addr.id} className="mb-4 p-4 border rounded-lg shadow">
          <div className="flex justify-between">
            <div>
              <p className="font-semibold">{addr.receiverName} <span className="text-gray-500">({addr.phoneNumber})</span></p>
              <p>{addr.detail}</p>
              <p> {addr.ward.name}, {addr.district.name}, {addr.province.name} </p>
              {addr.primary && <span className="text-md text-red-500 text-sm font-semibold border-2 border-solid border-red-100">Mặc định</span>}
            </div>
            <div className="flex flex-col items-end gap-2">
              <div className="text-blue-500 ">
                <button className="cursor-pointer mr-4">Sửa</button>
                <button className="cursor-pointer">Xóa</button>
              </div>
              {!addr.primary && <button className="cursor-pointer border-1 border-solid p-1 hover:bg-gray-100 rounded-sm">Đặt làm mặc định</button>}
            </div>
          </div>
        </div>
      ))}

      {open && (
        <div className="fixed inset-0 flex items-center justify-center bg-gray-100/85">
          <div className="bg-white p-6 rounded-lg shadow-lg w-150">
            <h3 className="text-lg font-semibold mb-4">Địa chỉ mới</h3>

            <div className="space-y-3">
              <div className="flex gap-3">
                <input
                  type="text"
                  name="receiverName"
                  placeholder="Họ tên người nhận"
                  value={newAddress.receiverName}
                  onChange={handleChangeNewAddress}
                  className="w-1/2 p-2 border rounded"
                />
                <input
                  type="text"
                  name="phoneNumber"
                  placeholder="Số điện thoại"
                  value={newAddress.phoneNumber}
                  onChange={handleChangeNewAddress}
                  className="w-1/2 p-2 border rounded"
                />
              </div>

              <div className="flex gap-3">
                <select
                  name="province"
                  value={newAddress.province}
                  onChange={handleChangeNewAddress}
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
                  value={newAddress.district}
                  onChange={handleChangeNewAddress}
                  className="w-full p-2 border rounded"
                  disabled={!newAddress.province}
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
                  value={newAddress.ward}
                  onChange={handleChangeNewAddress}
                  className="w-full p-2 border rounded"
                  disabled={!newAddress.district}
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
                value={newAddress.detail}
                onChange={handleChangeNewAddress}
                className="w-full p-2 border rounded"
              />

              <label>
                <input
                  type="checkbox"
                  name="primary"
                  checked={newAddress.primary}
                  onChange={handleChangeNewAddress}
                />
                Đặt làm mặc định
              </label>

              <div className="flex justify-between items-center mt-4">
                <button className="text-gray-500 hover:underline" onClick={() => setOpen(false)}>Hủy</button>
                <button className="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600" onClick={() => handleAddNewAddress()} >
                  Thêm
                </button>
              </div>
            </div>
          </div>
          
        </div>
      )}
    </div>
  );
};
