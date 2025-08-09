import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import BasicLayout from './layout/BasicLayout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import NetworkAnalysis from './pages/NetworkAnalysis';
import TrafficReplay from './pages/TrafficReplay';
import AppTracing from './pages/AppTracing';
import FaultAnalysis from './pages/FaultAnalysis';
import Arthas from './pages/Arthas';
import AppDependencies from './pages/AppDependencies';
import StateManagementDemo from './components/StateManagementDemo';
import 'antd/dist/reset.css';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<BasicLayout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="state-demo" element={<StateManagementDemo />} />
            <Route
              path="cluster/network-analysis"
              element={<NetworkAnalysis />}
            />
            <Route path="cluster/traffic-replay" element={<TrafficReplay />} />
            <Route path="app-tracing" element={<AppTracing />} />
            <Route path="fault-analysis" element={<FaultAnalysis />} />
            <Route path="tools/arthas" element={<Arthas />} />
            <Route path="app-dependencies" element={<AppDependencies />} />
          </Route>
        </Routes>
      </Router>
    </ConfigProvider>
  );
}

export default App;
