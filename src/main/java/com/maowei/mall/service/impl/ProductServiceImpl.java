package com.maowei.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.maowei.mall.dao.ProductMapper;
import com.maowei.mall.enums.ProductStatusEnum;
import com.maowei.mall.enums.ResponseEnum;
import com.maowei.mall.pojo.Product;
import com.maowei.mall.service.ICategoryService;
import com.maowei.mall.service.IProductService;
import com.maowei.mall.vo.ProductDetailVo;
import com.maowei.mall.vo.ProductVo;
import com.maowei.mall.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements IProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 查出某一类别（CategoryId）的所有商品，包括它的次级目录的所有商品
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null) {
            // 当categoryId为null是，即request中为传入值时，不将null添加到集合中，默认查所有商品
            categoryService.findSubCategoryId(categoryId, categoryIdSet); // 在mall_category表中查出categoryId所有子目录的categoryId
            categoryIdSet.add(categoryId); // 将父目录放进categoryIdSet中
        }

        PageHelper.startPage(pageNum, pageSize); // pageNUm-从第几页开始取，pageSize-一页几条数据
        List<Product> products = productMapper.selectByCategoryIdSet(categoryIdSet); // 在mall_product表中查出，categoryId对应的商品
        logger.info("products={}", products);
        List<ProductVo> productVoList = new ArrayList<>();
        for (Product product : products) {
            productVoList.add(product2ProductVo(product));
        }

        PageInfo pageInfo = new PageInfo<>(products);
        pageInfo.setList(productVoList);
        return ResponseVo.success(pageInfo);
    }


    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        // 这里用或，而不用取反的原因是，如果以后增加了商品状态，比如4-促销状态，用取反就会报错
        // 只对确定性条件判断
        if (product.getStatus().equals(ProductStatusEnum.OFF_SALE.getCode())
                || product.getStatus().equals(ProductStatusEnum.DELETE.getCode())) {
            return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
        }
        ProductDetailVo productDetailVo = product2ProductDetailVo(product);
        return ResponseVo.success(productDetailVo);
    }


    public ProductVo product2ProductVo(Product product) {
        ProductVo productVo = new ProductVo();
        BeanUtils.copyProperties(product, productVo);
        return productVo;
    }

    public ProductDetailVo product2ProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);
        // 敏感数据处理，不想让其他人知道真实的库存值，如果库存大于100，设置成100
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
        return  productDetailVo;
    }
}
