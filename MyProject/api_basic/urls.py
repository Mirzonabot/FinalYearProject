from django.urls import path, include
from .views import article_detail, ArticleAPIView,ArticleDetails,GenericAPIView,ArticleViewSet,ArticklGenericViewSet,ArticleModelViewSet
from rest_framework.routers import DefaultRouter

router = DefaultRouter()
# router.register('article',ArticleViewSet,basename='article')
# router.register('article',ArticklGenericViewSet,basename='article')
router.register('article',ArticleModelViewSet,basename='article')

urlpatterns = [
    # path('viewset/',include(router.urls)),
    # path('viewset/<int:pk>',include(router.urls)),
    # path('genericviewset/',include(router.urls)),
    # path('genericviewset/<int:pk>',include(router.urls)),
    # path('genericviewset/',include(router.urls)),
    # path('genericviewset/<int:pk>',include(router.urls)),
    path('modelviewset/',include(router.urls)),
    path('modelviewset/<int:pk>',include(router.urls)),
    # path('article/',article_list),
    path('article/',ArticleAPIView.as_view()),     
    # path('detail/<int:pk>',article_detail),
    path('detail/<int:id>',ArticleDetails.as_view()),
    path('generic/article/<int:id>',GenericAPIView.as_view()),
    path('generic/article/',GenericAPIView.as_view()),
]
