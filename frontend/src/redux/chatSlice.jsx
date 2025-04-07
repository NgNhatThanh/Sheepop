import { createSlice } from "@reduxjs/toolkit";

const chatSlice = createSlice({
    name: "chat",
    initialState: {
        userId: null,  // ID của user đang chat
    },
    reducers: {
        setUserId: (state, action) => {
            state.userId = action.payload; // Cập nhật userId khi nhấn vào chat
        }
    }
});

export const { setUserId } = chatSlice.actions;
export default chatSlice.reducer;
