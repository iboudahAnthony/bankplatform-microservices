const express = require('express');
const router = express.Router();
const notificationService = require('../services/notificationService');

// Envoyer une notification
router.post('/send', async (req, res) => {
  try {
    const { type, recipient, subject, message, data } = req.body;

    if (!type || !recipient || !message) {
      return res.status(400).json({
        error: 'type, recipient et message sont obligatoires'
      });
    }

    const result = await notificationService.sendNotification({
      type,
      recipient,
      subject,
      message,
      data
    });

    res.status(201).json(result);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Notifications liées aux transactions
router.post('/transaction', async (req, res) => {
  try {
    const { transactionId, type, amount, currency, recipient, accountNumber } = req.body;

    const message = generateTransactionMessage(type, amount, currency, accountNumber);
    const result = await notificationService.sendNotification({
      type: 'SMS',
      recipient,
      subject: 'Transaction effectuée',
      message,
      data: { transactionId, amount, currency }
    });

    res.status(201).json(result);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Notifications liées aux prêts
router.post('/loan', async (req, res) => {
  try {
    const { loanId, status, amount, recipient, reason } = req.body;

    let message = '';
    if (status === 'APPROVED') {
      message = `Votre demande de prêt de ${amount} XOF a été approuvée. Ref: ${loanId}`;
    } else if (status === 'REJECTED') {
      message = `Votre demande de prêt a été rejetée. Motif: ${reason}`;
    } else if (status === 'REPAYMENT') {
      message = `Remboursement de ${amount} XOF enregistré pour votre prêt Ref: ${loanId}`;
    }

    const result = await notificationService.sendNotification({
      type: 'EMAIL',
      recipient,
      subject: `Prêt - ${status}`,
      message,
      data: { loanId, status, amount }
    });

    res.status(201).json(result);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Lister l'historique des notifications (en mémoire pour simplicité)
router.get('/history', (req, res) => {
  res.json(notificationService.getHistory());
});

function generateTransactionMessage(type, amount, currency, accountNumber) {
  const acc = accountNumber ? `compte ${accountNumber}` : 'votre compte';
  switch (type) {
    case 'DEPOT':
      return `Dépôt de ${amount} ${currency} effectué sur ${acc}`;
    case 'RETRAIT':
      return `Retrait de ${amount} ${currency} effectué depuis ${acc}`;
    case 'TRANSFERT_INTRA':
    case 'TRANSFERT_INTER':
      return `Transfert de ${amount} ${currency} effectué depuis ${acc}`;
    default:
      return `Transaction de ${amount} ${currency} effectuée`;
  }
}

module.exports = router;
