from django.shortcuts import render
from django.forms.models import model_to_dict
from django.http import JsonResponse, HttpResponse

import json
from products.models import Product
from rest_framework.response import Response
from rest_framework.decorators import api_view
from products.serializers import ProductSerializer
# Create your views here.

def api_home(request,*args, **kwargs):
    # print(request.GET)
    # print(request.POST)
    # body = request.body
    # data = {}
    # try:
    #     data = json.loads(body)
    # except:
    #     pass
    # data['params'] = dict(request.GET)
    # data['headers'] = dict(request.headers)
    # data['content_type'] = request.content_type
    model_data = Product.objects.all().order_by("?").first()
    data = {}
    if model_data:
        # data['id'] = model_data.id
        # data['title'] = model_data.title
        # data['content'] = model_data.content
        # data['price'] = model_data.price
        data = model_to_dict(model_data,fields=['id','title','price'])
        # data = dict(data)
        # json_data_str = json.dumps(data)
        print(data)
    # return HttpResponse(json_data_str,headers = {"content-type":"application/json"})
    return JsonResponse(data)


@api_view(["GET","POST"])

def api_view_home(request,*args, **kwargs):
    """
    DRF API View
    """
    # model_data = Product.objects.all().order_by("?").first()
    instance = Product.objects.all().order_by("?").first()
    data = {}
    # if model_data:
        # data = model_to_dict(model_data,fields=['id','title','price'])
    if instance:
        data = ProductSerializer(instance).data


    return Response(data)