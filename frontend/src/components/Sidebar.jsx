import { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

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

  const isAdmin = ['SUPER_ADMIN', 'OPERATOR_ADMIN', 'OPERATOR_ANALYST'].includes(user?.role)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isActive = (path) => location.pathname === path

  return (
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
          display: 'flex',
          alignItems: 'center',
          gap: '0.7rem',
        }}>
          <div style={{
            width: '38px', height: '38px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #6366f1, #8b5cf6)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'white', fontWeight: 700, fontSize: '1rem', flexShrink: 0,
          }}>
            {user?.firstName?.[0]}{user?.lastName?.[0]}
          </div>
          <div style={{ overflow: 'hidden' }}>
            <div style={{ color: 'white', fontWeight: 600, fontSize: '0.85rem', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
              {user?.firstName} {user?.lastName}
            </div>
            <div style={{
              color: '#94a3b8', fontSize: '0.72rem',
              background: 'rgba(99,102,241,0.2)', padding: '1px 6px',
              borderRadius: '4px', display: 'inline-block', marginTop: '2px',
            }}>
              {user?.role}
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
  )
}
