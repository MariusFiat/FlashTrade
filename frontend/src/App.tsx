import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'

// Pages
import LoginPage from './pages/login/page'
import RegisterPage from './pages/register/page'
import DashboardPage from './pages/dashboard/page'
import AdminPage from './pages/admin/page'
import PortfolioPage from './pages/dashboard/portfolio/page'
import WalletPage from './pages/dashboard/wallet/page'
import MarketsPage from './pages/dashboard/markets/page'
import HistoryPage from './pages/dashboard/history/page'
import AccountPage from './pages/dashboard/account/page'
import AIInsightsPage from './pages/dashboard/ai-insights/page'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/dashboard/portfolio" element={<PortfolioPage />} />
        <Route path="/dashboard/wallet" element={<WalletPage />} />
        <Route path="/dashboard/markets" element={<MarketsPage />} />
        <Route path="/dashboard/history" element={<HistoryPage />} />
        <Route path="/dashboard/account" element={<AccountPage />} />
        <Route path="/dashboard/ai-insights" element={<AIInsightsPage />} />
        <Route path="/admin" element={<AdminPage />} />
      </Routes>
    </Router>
  )
}

export default App
