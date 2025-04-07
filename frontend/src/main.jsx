import { createRoot } from 'react-dom/client'
import {BrowserRouter as Router} from 'react-router-dom';
import './index.css'
import App from './App.jsx'
import { Provider } from "react-redux";
import { store } from "./redux/store.jsx";

createRoot(document.getElementById('root')).render(
  // <StrictMode>
  <Provider store={store}>
    <Router>
      <App />
    </Router>
  </Provider>
  // </StrictMode>,
)
