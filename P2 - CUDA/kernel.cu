#include <iostream>
#include <fstream>
#include <cuda.h>
#include <cuda_runtime.h>
#include <device_launch_parameters.h>
#include <stdio.h>
#include <stdlib.h>
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <stdio.h>
using namespace std;
#define TILE_WIDTH 32

#define WIDTH 1080
#define HEIGHT 1080 

#define MaxRGB 256 

typedef struct {
	unsigned int red;
	unsigned int green;
	unsigned int blue;
} RGB;

typedef struct {
	RGB* image;
	unsigned int width;
	unsigned int height;
} Mandelbrot;

__global__ void mandelbrotKernel(Mandelbrot mandelbrot, double* cr, double* ci) {
	
	__shared__ double ci_s[HEIGHT];
	__shared__ double cr_s[WIDTH];
	int row = blockIdx.y * blockDim.y + threadIdx.y;
	int col = blockIdx.x * blockDim.x + threadIdx.x;
	if (row > mandelbrot.height || col > mandelbrot.width) return;

	int index = row * mandelbrot.width + col;

	ci_s[row] = ci[row];
	cr_s[col] = cr[col];

	int i = 0;
	double zr = 0.0;
	double zi = 0.0;

	const int maxIterations = 500; 

	while (i < maxIterations && zr * zr + zi * zi < 4.0) {// converge catre infinit?
		double fz = zr * zr - zi * zi + cr_s[col];
		zi = 2.0 * zr * zi + ci_s[row];
		zr = fz;
		i++;
	}

	int r, g, b;
	int maxRGB = 256;
	int max3 = maxRGB;
	double t = (double)i / (double)maxIterations;
	i = (int)(t * (double)max3);
	b = i / (maxRGB * maxRGB);
	int nn = i - b * maxRGB;
	r = nn / maxRGB;
	g = nn - r * maxRGB;
	mandelbrot.image[index].red = r;
	mandelbrot.image[index].green = g;
	mandelbrot.image[index].blue = b;
}

int getCValues(double* c, int state, double beginRange, double endRange, double minVal, double maxVal);
cudaError_t mandelbrotSetWithCUDA(Mandelbrot mandelbrot, double* cr, double* ci);

int main()
{
	unsigned int width,height,maxRGB;

	width = WIDTH; 
	height = HEIGHT; 
	maxRGB = MaxRGB; 
	Mandelbrot mandelbrot;

	double* cr;
	double* ci;

	double minValR = -2.0;
	double maxValR = 1.0;
	double minValI = -1.5;
	double maxValI = 1.5;

	size_t size;

	mandelbrot.width = width;
	mandelbrot.height = height;
	
	size = width * height * sizeof(RGB);
	mandelbrot.image = (RGB*)malloc(size);


	size = width * sizeof(double);
	cr = (double*)malloc(size);
	size = height * sizeof(double);
	ci = (double*)malloc(size);

	getCValues(cr, 0, 0, width, minValR, maxValR);
	getCValues(ci, 0, 0, height, minValI, maxValI);

	cudaError_t cudaStatus = mandelbrotSetWithCUDA(mandelbrot, cr, ci);

	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Kernel fail");
	}
	else {
		printf("Generating image...\n");
		ofstream fout("output_image.ppm");
		fout << "P3" << endl;
		fout << mandelbrot.width << " " << mandelbrot.height << endl;
		fout << maxRGB << endl;
		
		for (int h = 0; h < height; h++) {
			
			for (int w = 0; w < width; w += 2) {			
				int index = h * width + w;
				fout << mandelbrot.image[index].red << " " << mandelbrot.image[index].green << " " << mandelbrot.image[index].blue << " ";
				fout << mandelbrot.image[index + 1].red<< " " << mandelbrot.image[index + 1].green << " " << mandelbrot.image[index + 1].blue << " ";
			}
			fout << endl;
		}
		fout.close();

		printf("Succes!\n");
	}
	return 0;
}

int getCValues(double* c, int state, double beginRange, double endRange, double minVal, double maxVal) {
	if (state < endRange) {
		c[state] = ((state - beginRange) / (endRange - beginRange))*(maxVal - minVal) + minVal;
		return getCValues(c, state + 1, beginRange, endRange, minVal, maxVal);
	}
	else {
		return 0;
	}
}

cudaError_t mandelbrotSetWithCUDA(Mandelbrot mandelbrot, double* cr, double* ci)
{
	cudaError_t cudaStatus;

	unsigned int width = mandelbrot.width;
	unsigned int height = mandelbrot.height;
	cudaStatus = cudaSetDevice(0);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaSetDevice fail");
	}

	Mandelbrot mandelbrot_d;
	mandelbrot_d.width = width;
	mandelbrot_d.height = height;
	size_t  mandlebortSize = width * height * sizeof(RGB);
	cudaStatus = cudaMalloc((void **)&mandelbrot_d.image, mandlebortSize);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc fail");
	}

	double* cr_d;
	size_t CRealSize = width * sizeof(double);
	cudaStatus = cudaMalloc((void**)&cr_d, CRealSize);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Real cudaMalloc fail");
	}

	double* ci_d;
	size_t  CImagSize = height * sizeof(double);
	cudaStatus = cudaMalloc((void**)&ci_d, CImagSize);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Imaginari cudaMalloc fail");
	}

	cudaStatus = cudaMemcpy(cr_d, cr, CRealSize, cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Real cudaMemcpy fail");
	}

	cudaStatus = cudaMemcpy(ci_d, ci, CImagSize, cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Imaginari cudaMemcpy fail");
	}

	int blocks_x = (width + TILE_WIDTH - 1) / TILE_WIDTH;
	int blocks_y = (height + TILE_WIDTH - 1) / TILE_WIDTH;

	dim3 dimGrid(blocks_x, blocks_y, 1);
	dim3 dimBlock(TILE_WIDTH, TILE_WIDTH, 1);

	mandelbrotKernel << <dimGrid, dimBlock >> > (mandelbrot_d, cr_d, ci_d);

	cudaStatus = cudaGetLastError();
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "mandelbrotKernel fail: %s\n", cudaGetErrorString(cudaStatus));
	}


	cudaStatus = cudaDeviceSynchronize();
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaDeviceSynchronize fail : %d\n", cudaStatus);
		goto DeallocateMemory;
	}

	cudaStatus = cudaMemcpy(mandelbrot.image, mandelbrot_d.image, mandlebortSize, cudaMemcpyDeviceToHost);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy fail");
	}

DeallocateMemory:
	cudaFree(mandelbrot_d.image);
	cudaFree(cr_d);
	cudaFree(ci_d);
	return cudaStatus;
}
