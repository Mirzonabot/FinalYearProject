from django.urls import path 

from . import views




urlpatterns = [
    path('',views.api_home),
    path('home_api_view/',views.api_view_home),
    
]