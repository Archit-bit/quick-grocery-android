const express = require('express');
const router = express.Router();
const auth = require('../middleware/authMiddleware');
const cartController = require('../controllers/cartController');

// All cart routes require authentication
router.get('/', auth, cartController.getCart);
router.post('/', auth, cartController.addItemToCart);
router.put('/:productId', auth, cartController.updateCartItemQuantity);
router.delete('/:productId', auth, cartController.removeItemFromCart);

module.exports = router;
