import axios from 'axios'

// URLs directes des services (mode développement sans Gateway)
export const SERVICES = {
  auth:         'http://localhost:8081',
  customer:     'http://localhost:8082',
  account:      'http://localhost:8083',
  transaction:  'http://localhost:8084',
  loan:         'http://localhost:8086',
  notification: 'http://localhost:8089',
}

// Fonction pour créer un client axios par service
const createClient = (baseURL) => {
  const client = axios.create({
    baseURL,
    headers: { 'Content-Type': 'application/json' }
  })

  // Ajouter le token JWT automatiquement
  client.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  })

  // Gérer les 401
  client.interceptors.response.use(
    response => response,
    error => {
      if (error.response?.status === 401) {
        localStorage.clear()
        window.location.href = '/login'
      }
      return Promise.reject(error)
    }
  )
  return client
}

// Clients par service
export const authApi         = createClient(SERVICES.auth)
export const customerApi     = createClient(SERVICES.customer)
export const accountApi      = createClient(SERVICES.account)
export const transactionApi  = createClient(SERVICES.transaction)
export const loanApi         = createClient(SERVICES.loan)
export const notificationApi = createClient(SERVICES.notification)

// Client par défaut (auth)
export default authApi
