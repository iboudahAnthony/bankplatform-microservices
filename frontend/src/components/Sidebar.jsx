import { useState, useEffect } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { notificationApi } from '../api/axios'
import axios from 'axios'

const SERVICES_STATUS = [
  { name: 'Auth',         url: 'http://localhost:8081/auth/health' },
  { name: 'Customer',     url: 'http://localhost:8082/customers/health' },
  { name: 'Account',      url: 'http://localhost:8083/accounts/health' },
  { name: 'Transaction',  url: 'http://localhost:8084/transactions/health' },
  { name: 'Loan',         url: 'http://localhost:8086/loans/health' },
  { name: 'Document',     url: 'http://localhost:8087/documents/health' },
  { name: 'Notification', url: 'http://localhost:8089/notifications/health' },
]

const menuItems = [
  { path: '/dashboard',    icon: '⊞',  label: 'Tableau de bord' },
  { path: '/accounts',     icon: '💳',  label: 'Comptes' },
  { path: '/transactions', icon: '↔',  label: 'Transactions' },
  { path: '/loans',        icon: '📋',  label: 'Prêts' },
  { path: '/documents',    icon: '📄',  label: 'Documents & KYC' },
]

const adminItems = [
  { path: '/customers', icon: '👥', label: 'Clients' },
]

export default function Sidebar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [collapsed, setCollapsed] = useState(false)
  const [notifications, setNotifications] = useState([])
  const [showNotif, setShowNotif] = useState(false)
  const [servicesStatus, setServicesStatus] = useState([])
  const [showStatus, setShowStatus] = useState(false)

  useEffect(() => {
    fetchNotifications()
    checkServicesStatus()
    const interval = setInterval(() => {
      fetchNotifications()
      checkServicesStatus()
    }, 30000)
    return () => clearInterval(interval)
  }, [])

  const fetchNotifications = async () => {
    try {
      const res = await notificationApi.get('/notifications/history')
      setNotifications(res.data || [])
    } catch { }
  }

  const checkServicesStatus = async () => {
    const results = await Promise.all(
      SERVICES_STATUS.map(async (svc) => {
        try {
          await axios.get(svc.url, { timeout: 3000 })
          return { ...svc, status: 'UP' }
        } catch {
          return { ...svc, status: 'DOWN' }
        }
      })
    )
    setServicesStatus(results)
  }

  const allUp = servicesStatus.length > 0 && servicesStatus.every(s => s.status === 'UP')
  const someDown = servicesStatus.some(s => s.status === 'DOWN')
  const unreadCount = notifications.filter(n => n.status === 'SENT').length

  const isAdmin = ['SUPER_ADMIN', 'OPERATOR_ADMIN', 'OPERATOR_ANALYST'].includes(user?.role)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isActive = (path) => location.pathname === path

  return (
    <>
    <aside style={{
      width: collapsed ? '72px' : '240px',
      minHeight: '100vh',
      background: 'linear-gradient(180deg, #0f172a 0%, #1e293b 100%)',
      display: 'flex',
      flexDirection: 'column',
      transition: 'width 0.25s ease',
      position: 'fixed',
      left: 0,
      top: 0,
      zIndex: 100,
      boxShadow: '4px 0 24px rgba(0,0,0,0.18)',
    }}>

      {/* Logo + collapse */}
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: collapsed ? 'center' : 'space-between',
        padding: '1.2rem 1rem',
        borderBottom: '1px solid rgba(255,255,255,0.07)',
      }}>
        {!collapsed && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
            <span style={{ fontSize: '1.5rem' }}>🏦</span>
            <span style={{ color: 'white', fontWeight: 700, fontSize: '1.05rem', letterSpacing: '0.5px' }}>
              BankPlatform
            </span>
          </div>
        )}
        {collapsed && <span style={{ fontSize: '1.5rem' }}>🏦</span>}
        <button
          onClick={() => setCollapsed(!collapsed)}
          style={{
            background: 'rgba(255,255,255,0.08)',
            border: 'none',
            borderRadius: '6px',
            color: 'white',
            cursor: 'pointer',
            padding: '4px 8px',
            fontSize: '0.8rem',
            display: collapsed ? 'none' : 'block',
          }}>
          ◀
        </button>
      </div>

      {/* Profil utilisateur */}
      {!collapsed && (
        <div style={{
          padding: '1rem',
          margin: '0.8rem',
          background: 'rgba(255,255,255,0.05)',
          borderRadius: '10px',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.7rem' }}>
            <div style={{
              width: '38px', height: '38px', borderRadius: '50%',
              background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: 'white', fontWeight: 700, fontSize: '1rem', flexShrink: 0,
            }}>
              {user?.firstName?.[0]}{user?.lastName?.[0]}
            </div>
            <div style={{ flex: 1, overflow: 'hidden' }}>
              <div style={{ color: 'white', fontWeight: 600, fontSize: '0.85rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {user?.firstName} {user?.lastName}
              </div>
              <div style={{ color: '#94a3b8', fontSize: '0.72rem', background: 'rgba(99,102,241,0.2)', padding: '1px 6px', borderRadius: '4px', display: 'inline-block', marginTop: '2px' }}>
                {user?.role}
              </div>
            </div>
            {/* Cloche notifications */}
            <div style={{ position: 'relative' }}>
              <button onClick={() => setShowNotif(!showNotif)} style={{
                background: 'rgba(255,255,255,0.08)', border: 'none',
                borderRadius: '8px', color: 'white', cursor: 'pointer',
                padding: '6px 8px', fontSize: '1rem', position: 'relative',
              }}>
                🔔
                {unreadCount > 0 && (
                  <span style={{
                    position: 'absolute', top: '-4px', right: '-4px',
                    background: '#ef4444', color: 'white', borderRadius: '50%',
                    width: '16px', height: '16px', fontSize: '0.65rem',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontWeight: 700,
                  }}>
                    {unreadCount > 9 ? '9+' : unreadCount}
                  </span>
                )}
              </button>

              {/* Panneau notifications */}
              {showNotif && (
                <div style={{
                  position: 'absolute', top: '40px', left: '0',
                  background: 'white', borderRadius: '12px', width: '300px',
                  boxShadow: '0 8px 32px rgba(0,0,0,0.2)', zIndex: 1000,
                  maxHeight: '350px', overflow: 'hidden',
                }}>
                  <div style={{ padding: '0.75rem 1rem', borderBottom: '1px solid #f1f5f9', display: 'flex', justifyContent: 'space-between' }}>
                    <strong style={{ color: '#0f172a', fontSize: '0.9rem' }}>Notifications</strong>
                    <span style={{ fontSize: '0.75rem', color: '#64748b' }}>{notifications.length}</span>
                  </div>
                  <div style={{ overflowY: 'auto', maxHeight: '280px' }}>
                    {notifications.length === 0 ? (
                      <p style={{ padding: '1.5rem', textAlign: 'center', color: '#94a3b8', fontSize: '0.85rem' }}>
                        Aucune notification
                      </p>
                    ) : (
                      notifications.slice(0, 8).map((notif, idx) => (
                        <div key={idx} style={{
                          padding: '0.75rem 1rem',
                          borderBottom: '1px solid #f8fafc',
                          background: notif.status === 'SENT' ? '#f8faff' : 'white',
                        }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '3px' }}>
                            <span style={{ fontSize: '0.72rem', fontWeight: 600, color: '#6366f1', background: '#eef2ff', padding: '1px 6px', borderRadius: '4px' }}>
                              {notif.type}
                            </span>
                            <span style={{ fontSize: '0.7rem', color: '#94a3b8' }}>
                              {notif.sentAt ? new Date(notif.sentAt).toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) : ''}
                            </span>
                          </div>
                          <p style={{ fontSize: '0.8rem', color: '#334155', margin: 0 }}>{notif.message}</p>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Navigation */}
      <nav style={{ flex: 1, padding: '0.5rem 0.6rem', overflowY: 'auto' }}>
        {!collapsed && (
          <div style={{ color: '#64748b', fontSize: '0.7rem', fontWeight: 700, letterSpacing: '1px', padding: '0.5rem 0.6rem 0.3rem', textTransform: 'uppercase' }}>
            Navigation
          </div>
        )}

        {menuItems.map(item => (
          <Link
            key={item.path}
            to={item.path}
            title={collapsed ? item.label : ''}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem',
              padding: collapsed ? '0.75rem' : '0.7rem 0.8rem',
              borderRadius: '8px',
              marginBottom: '2px',
              textDecoration: 'none',
              color: isActive(item.path) ? 'white' : '#94a3b8',
              background: isActive(item.path)
                ? 'linear-gradient(135deg, #6366f1, #8b5cf6)'
                : 'transparent',
              transition: 'all 0.15s',
              justifyContent: collapsed ? 'center' : 'flex-start',
              fontWeight: isActive(item.path) ? 600 : 400,
              fontSize: '0.9rem',
            }}
            onMouseEnter={e => {
              if (!isActive(item.path))
                e.currentTarget.style.background = 'rgba(255,255,255,0.06)'
            }}
            onMouseLeave={e => {
              if (!isActive(item.path))
                e.currentTarget.style.background = 'transparent'
            }}
          >
            <span style={{ fontSize: '1.1rem', flexShrink: 0 }}>{item.icon}</span>
            {!collapsed && <span>{item.label}</span>}
          </Link>
        ))}

        {isAdmin && (
          <>
            {!collapsed && (
              <div style={{ color: '#64748b', fontSize: '0.7rem', fontWeight: 700, letterSpacing: '1px', padding: '0.8rem 0.6rem 0.3rem', textTransform: 'uppercase' }}>
                Administration
              </div>
            )}
            {adminItems.map(item => (
              <Link
                key={item.path}
                to={item.path}
                title={collapsed ? item.label : ''}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.75rem',
                  padding: collapsed ? '0.75rem' : '0.7rem 0.8rem',
                  borderRadius: '8px',
                  marginBottom: '2px',
                  textDecoration: 'none',
                  color: isActive(item.path) ? 'white' : '#94a3b8',
                  background: isActive(item.path)
                    ? 'linear-gradient(135deg, #6366f1, #8b5cf6)'
                    : 'transparent',
                  justifyContent: collapsed ? 'center' : 'flex-start',
                  fontWeight: isActive(item.path) ? 600 : 400,
                  fontSize: '0.9rem',
                  transition: 'all 0.15s',
                }}>
                <span style={{ fontSize: '1.1rem' }}>{item.icon}</span>
                {!collapsed && <span>{item.label}</span>}
              </Link>
            ))}
          </>
        )}
      </nav>

      {/* Bouton collapse (bas) quand collapsed */}
      {collapsed && (
        <button
          onClick={() => setCollapsed(false)}
          style={{
            background: 'rgba(255,255,255,0.08)',
            border: 'none', color: 'white', cursor: 'pointer',
            padding: '0.6rem', fontSize: '0.9rem', margin: '0.5rem',
            borderRadius: '8px',
          }}>
          ▶
        </button>
      )}

      {/* Statut système */}
      <div style={{ padding: '0 0.8rem', marginBottom: '0.5rem' }}>
        <button
          onClick={() => setShowStatus(!showStatus)}
          style={{
            width: '100%', background: 'rgba(255,255,255,0.05)',
            border: `1px solid ${allUp ? 'rgba(34,197,94,0.3)' : someDown ? 'rgba(239,68,68,0.3)' : 'rgba(255,255,255,0.1)'}`,
            borderRadius: '8px', padding: collapsed ? '0.6rem' : '0.6rem 0.8rem',
            cursor: 'pointer', display: 'flex', alignItems: 'center',
            justifyContent: collapsed ? 'center' : 'space-between', gap: '0.5rem',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{
              width: 8, height: 8, borderRadius: '50%', flexShrink: 0,
              background: allUp ? '#22c55e' : someDown ? '#ef4444' : '#f59e0b',
              boxShadow: `0 0 6px ${allUp ? '#22c55e' : someDown ? '#ef4444' : '#f59e0b'}`,
              animation: 'pulse 2s infinite',
            }} />
            {!collapsed && (
              <span style={{ color: '#94a3b8', fontSize: '0.78rem', fontWeight: 500 }}>
                {allUp ? 'Tous services UP' : someDown ? 'Service(s) DOWN' : 'Vérification...'}
              </span>
            )}
          </div>
          {!collapsed && servicesStatus.length > 0 && (
            <span style={{ color: '#64748b', fontSize: '0.7rem' }}>
              {servicesStatus.filter(s => s.status === 'UP').length}/{servicesStatus.length}
            </span>
          )}
        </button>

        {/* Panneau détail statut */}
        {showStatus && !collapsed && (
          <div style={{
            background: '#0f172a', border: '1px solid rgba(255,255,255,0.1)',
            borderRadius: '10px', padding: '0.75rem', marginTop: '0.5rem',
          }}>
            <p style={{ color: '#64748b', fontSize: '0.7rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: '0.6rem' }}>
              État des microservices
            </p>
            {servicesStatus.length === 0 ? (
              <p style={{ color: '#64748b', fontSize: '0.78rem' }}>Vérification...</p>
            ) : (
              servicesStatus.map((svc, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '3px 0' }}>
                  <span style={{ color: '#94a3b8', fontSize: '0.78rem' }}>{svc.name}</span>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <span style={{
                      width: 6, height: 6, borderRadius: '50%',
                      background: svc.status === 'UP' ? '#22c55e' : '#ef4444',
                    }} />
                    <span style={{
                      fontSize: '0.7rem', fontWeight: 600,
                      color: svc.status === 'UP' ? '#22c55e' : '#ef4444',
                    }}>
                      {svc.status}
                    </span>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      {/* Déconnexion */}
      <div style={{ padding: '0.8rem', borderTop: '1px solid rgba(255,255,255,0.07)' }}>
        <button
          onClick={handleLogout}
          style={{
            width: '100%',
            background: 'rgba(239,68,68,0.12)',
            border: '1px solid rgba(239,68,68,0.2)',
            borderRadius: '8px',
            color: '#f87171',
            cursor: 'pointer',
            padding: collapsed ? '0.7rem' : '0.6rem 1rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: collapsed ? 'center' : 'flex-start',
            gap: '0.6rem',
            fontSize: '0.88rem',
            fontWeight: 500,
            transition: 'all 0.15s',
          }}
          onMouseEnter={e => e.currentTarget.style.background = 'rgba(239,68,68,0.2)'}
          onMouseLeave={e => e.currentTarget.style.background = 'rgba(239,68,68,0.12)'}
        >
          <span>🚪</span>
          {!collapsed && <span>Déconnexion</span>}
        </button>
      </div>
    </aside>
    <style>{`
      @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.4; } }
    `}</style>
    </>
  )
}
