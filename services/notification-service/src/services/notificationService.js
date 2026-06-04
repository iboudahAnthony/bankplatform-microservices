const nodemailer = require('nodemailer');

// Historique en mémoire (en prod : MongoDB)
const notificationHistory = [];

// Transporter email (mode test avec Ethereal)
const createTransporter = () => {
  return nodemailer.createTransport({
    host: process.env.SMTP_HOST || 'smtp.ethereal.email',
    port: parseInt(process.env.SMTP_PORT || '587'),
    auth: {
      user: process.env.SMTP_USER || 'test@ethereal.email',
      pass: process.env.SMTP_PASS || 'testpassword'
    }
  });
};

const sendNotification = async ({ type, recipient, subject, message, data }) => {
  const notification = {
    id: Date.now().toString(),
    type,
    recipient,
    subject,
    message,
    data,
    status: 'PENDING',
    sentAt: null,
    createdAt: new Date().toISOString()
  };

  try {
    if (type === 'EMAIL') {
      await sendEmail(recipient, subject, message);
    } else if (type === 'SMS') {
      await sendSMS(recipient, message);
    } else if (type === 'PUSH') {
      await sendPush(recipient, subject, message);
    }

    notification.status = 'SENT';
    notification.sentAt = new Date().toISOString();
    console.log(`[NOTIFICATION] ${type} envoyé à ${recipient}: ${message}`);

  } catch (error) {
    notification.status = 'FAILED';
    notification.error = error.message;
    console.error(`[NOTIFICATION] Erreur envoi ${type} à ${recipient}:`, error.message);
  }

  notificationHistory.push(notification);
  return notification;
};

const sendEmail = async (to, subject, text) => {
  // En développement : on simule l'envoi
  console.log(`[EMAIL] À: ${to} | Sujet: ${subject} | Message: ${text}`);
  // En production, décommenter :
  // const transporter = createTransporter();
  // await transporter.sendMail({ from: 'bank@platform.com', to, subject, text });
};

const sendSMS = async (to, message) => {
  // En développement : on simule l'envoi SMS
  console.log(`[SMS] À: ${to} | Message: ${message}`);
  // En production : intégrer Twilio, Orange SMS API, etc.
};

const sendPush = async (to, title, body) => {
  // En développement : on simule l'envoi push
  console.log(`[PUSH] À: ${to} | Titre: ${title} | Corps: ${body}`);
};

const getHistory = () => notificationHistory;

module.exports = { sendNotification, getHistory };
