const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const admin = require('../middleware/adminMiddleware');
const adminController = require('../controllers/adminController');

router.get('/orders', auth, admin, adminController.getAllOrders);
router.get('/orders/:orderId', auth, admin, adminController.getOrderDetails);
router.put('/orders/:orderId/status', auth, admin, adminController.updateOrderStatus);
router.get('/inventory', auth, admin, adminController.getInventory);
router.put('/inventory/:productId', auth, admin, adminController.updateInventory);

module.exports = router;
