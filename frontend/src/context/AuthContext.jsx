import { createContext, useContext, useState, useEffect } from 'react'
import { authApi as api } from '../api/axios'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    const userData = localStorage.getItem('user')
    if (token && userData) {
      setUser(JSON.parse(userData))
    }
    setLoading(false)
  }, [])

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password })
    const data = response.data
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId,
      email: data.email,
      role: data.role,
      firstName: data.firstName,
      lastName: data.lastName
    }))
    setUser({ userId: data.userId, email: data.email, role: data.role,
              firstName: data.firstName, lastName: data.lastName })
    return data
  }

  const register = async (userData) => {
    const response = await api.post('/auth/register', userData)
    return response.data
  }

  const logout = () => {
    localStorage.clear()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {!loading && children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
