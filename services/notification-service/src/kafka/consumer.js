const { Kafka } = require('kafkajs')
const notificationService = require('../services/notificationService')

const kafka = new Kafka({
  clientId: 'notification-service',
  brokers: [process.env.KAFKA_BROKERS || 'localhost:9092'],
  retry: {
    initialRetryTime: 3000,
    retries: 5
  }
})

const consumer = kafka.consumer({ groupId: 'notification-group' })

const connectConsumer = async () => {
  try {
    await consumer.connect()
    console.log('[KAFKA] Consommateur connecté')

    // S'abonner aux topics
    await consumer.subscribe({
      topics: ['transaction.completed', 'transaction.failed'],
      fromBeginning: false
    })

    // Traiter les messages
    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        try {
          const event = JSON.parse(message.value.toString())
          console.log(`[KAFKA] Message reçu - Topic: ${topic}, Ref: ${event.reference}`)

          if (topic === 'transaction.completed') {
            await handleTransactionCompleted(event)
          } else if (topic === 'transaction.failed') {
            await handleTransactionFailed(event)
          }
        } catch (err) {
          console.error('[KAFKA] Erreur traitement message:', err.message)
        }
      }
    })
  } catch (err) {
    console.warn('[KAFKA] Impossible de se connecter à Kafka:', err.message)
    console.warn('[KAFKA] Le service fonctionnera sans Kafka')
  }
}

const handleTransactionCompleted = async (event) => {
  const { reference, type, amount, currency, initiatedBy } = event

  const typeMessages = {
    DEPOT: `Dépôt de ${amount} ${currency} effectué avec succès. Réf: ${reference}`,
    RETRAIT: `Retrait de ${amount} ${currency} effectué avec succès. Réf: ${reference}`,
    TRANSFERT_INTRA: `Transfert de ${amount} ${currency} effectué. Réf: ${reference}`,
    TRANSFERT_INTER: `Transfert inter-opérateur de ${amount} ${currency} effectué. Réf: ${reference}`,
  }

  const message = typeMessages[type] || `Transaction de ${amount} ${currency} complétée. Réf: ${reference}`

  await notificationService.sendNotification({
    type: 'SMS',
    recipient: initiatedBy || 'client',
    subject: 'Transaction effectuée',
    message,
    data: event
  })
}

const handleTransactionFailed = async (event) => {
  const { reference, type, amount, currency, initiatedBy } = event

  await notificationService.sendNotification({
    type: 'SMS',
    recipient: initiatedBy || 'client',
    subject: 'Transaction échouée',
    message: `Votre transaction de ${amount} ${currency} a échoué. Réf: ${reference}. Contactez votre conseiller.`,
    data: event
  })
}

module.exports = { connectConsumer }
