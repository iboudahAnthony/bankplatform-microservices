const express = require('express')
const cors = require('cors')
const morgan = require('morgan')
require('dotenv').config()

const notificationRoutes = require('./routes/notifications')
const { connectConsumer } = require('./kafka/consumer')

const app = express()
const PORT = process.env.PORT || 8089

// Middleware
app.use(cors())
app.use(express.json())
app.use(morgan('dev'))

// Routes
app.use('/notifications', notificationRoutes)

// Health check
app.get('/notifications/health', (req, res) => {
  res.json({ status: 'UP', service: 'notification-service' })
})

app.get('/', (req, res) => {
  res.json({ service: 'notification-service', status: 'running' })
})

// Démarrer le serveur
app.listen(PORT, () => {
  console.log(`Notification Service démarré sur le port ${PORT}`)
  // Connexion Kafka (non bloquante - fonctionne même sans Kafka)
  connectConsumer().catch(err =>
    console.warn('[KAFKA] Connexion différée:', err.message)
  )
})

module.exports = app
